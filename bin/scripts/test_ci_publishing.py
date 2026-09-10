import os
import subprocess
import unittest
from pathlib import Path
from tempfile import TemporaryDirectory
from unittest.mock import patch

import yaml

import upload


ROOT = Path(__file__).resolve().parents[2]


class CiPublishingTest(unittest.TestCase):
    def test_github_timestamp_survives_steps_and_artifact_handoff(self):
        for workflow in ("debug", "release", "pr"):
            with self.subTest(workflow=workflow), TemporaryDirectory() as directory:
                jobs = yaml.safe_load((ROOT / f".github/workflows/{workflow}.yml").read_text())["jobs"]
                steps = jobs["build"]["steps"]
                configure = next(step for step in steps if step.get("name") == "Configure Gradle")
                env_path = Path(directory) / "github.env"
                subprocess.run(
                    ["bash", "-eu", "-c", configure["run"]],
                    cwd=directory,
                    env={"PATH": os.environ["PATH"], "GITHUB_ENV": str(env_path), "ANDROID_HOME": "/unused"},
                    check=True,
                    capture_output=True,
                )
                build_env = dict(line.split("=", 1) for line in env_path.read_text().splitlines())
                timestamp = int(build_env["APP_BUILD_TIMESTAMP"])
                self.assertGreater(timestamp, 0)
                artifact = next(step["with"] for step in steps if step.get("name") == "Upload build timestamp")
                self.assertEqual("build-timestamp.txt", artifact["path"])
                self.assertEqual("error", artifact["if-no-files-found"])
                self.assertEqual(timestamp, int((Path(directory) / artifact["path"]).read_text()))
                # The upload process has a fresh environment, just the downloaded artifact.
                with patch.dict(os.environ, {}, clear=True), patch.object(upload, "artifacts_path", Path(directory)):
                    self.assertEqual(timestamp, upload.get_timestamp())
                self.assertFalse((Path(directory) / "gradle.properties").exists())
                if workflow != "pr":
                    upload_steps = jobs["upload"]["steps"]
                    download = next(step["with"] for step in upload_steps if step.get("name") == "Download build timestamp")
                    self.assertEqual(artifact["name"], download["name"])
                    self.assertEqual("artifacts", download["path"])

    def test_github_stable_and_test_routes_are_explicit(self):
        for workflow, variable, mode in (
            ("release", "UPDATE_METADATA_MESSAGE_ID", "release"),
            ("debug", "TEST_UPDATE_METADATA_MESSAGE_ID", "test"),
        ):
            with self.subTest(workflow=workflow):
                jobs = yaml.safe_load((ROOT / f".github/workflows/{workflow}.yml").read_text())["jobs"]
                publish = next(step for step in jobs["upload"]["steps"] if step.get("name") == "Send to Telegram")
                self.assertEqual("${{ vars." + variable + " }}", publish["env"]["UPDATE_METADATA_MESSAGE_ID"])
                self.assertIn(f'"$HELPER_BOT_TARGET" {mode}', publish["run"])

    def test_gitlab_exports_build_timestamp_and_delivers_it_to_upload(self):
        pipeline = yaml.safe_load((ROOT / ".gitlab-ci.yml").read_text())
        script = pipeline["build"]["script"]
        generate = next(line for line in script if line.startswith("export APP_BUILD_TIMESTAMP="))
        persist = next(line for line in script if "> artifacts/build-timestamp.txt" in line)
        with TemporaryDirectory() as directory:
            artifacts = Path(directory) / "artifacts"
            artifacts.mkdir()
            result = subprocess.run(
                ["bash", "-eu", "-c", generate + '\nsh -c \'printf "%s" "$APP_BUILD_TIMESTAMP"\'\n' + persist],
                cwd=directory,
                env={"PATH": os.environ["PATH"]},
                check=True,
                capture_output=True,
                text=True,
            )
            with patch.dict(os.environ, {}, clear=True), patch.object(upload, "artifacts_path", artifacts):
                self.assertEqual(int(result.stdout), upload.get_timestamp())
        self.assertIn("artifacts/build-timestamp.txt", pipeline["build"]["artifacts"]["paths"])
        self.assertIn({"job": "build", "artifacts": True}, pipeline["upload"]["needs"])
        self.assertIn("test -s artifacts/build-timestamp.txt", pipeline["upload"]["script"])


if __name__ == "__main__":
    unittest.main()
