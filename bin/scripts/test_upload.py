import contextlib
import io
import json
import os
import unittest
from pathlib import Path
from tempfile import TemporaryDirectory
from types import SimpleNamespace
from unittest.mock import AsyncMock, Mock, mock_open, patch

import upload


class TimestampTest(unittest.TestCase):
    def setUp(self):
        directory = self.enterContext(TemporaryDirectory())
        self.artifacts = Path(directory)
        self.enterContext(patch.object(upload, "artifacts_path", self.artifacts))
        self.enterContext(patch.dict(os.environ, {}, clear=True))

    def test_uses_exact_build_artifact_in_fresh_upload_job(self):
        (self.artifacts / "build-timestamp.txt").write_text("1789000123456\n")
        with patch.object(upload, "read_gradle_property") as properties:
            self.assertEqual(1789000123456, upload.get_timestamp())
        properties.assert_not_called()

    def test_environment_override_matches_gradle(self):
        with patch.dict(os.environ, APP_BUILD_TIMESTAMP="1789000123456"):
            self.assertEqual(1789000123456, upload.get_timestamp())

    def test_reproducible_properties_fallback(self):
        with patch("builtins.open", mock_open(read_data="APP_BUILD_TIMESTAMP=2000000000000\n")):
            self.assertEqual(2000000000000, upload.get_timestamp())

    def test_empty_environment_uses_properties_like_gradle(self):
        with patch.dict(os.environ, APP_BUILD_TIMESTAMP=""), patch.object(
            upload, "read_gradle_property", return_value="2000000000000"
        ):
            self.assertEqual(2000000000000, upload.get_timestamp())

    def test_malformed_artifact_never_falls_back(self):
        for value in ("", "bad", "0", "-1"):
            with self.subTest(value=value):
                (self.artifacts / "build-timestamp.txt").write_text(value)
                with patch.dict(os.environ, APP_BUILD_TIMESTAMP="2000000000000"):
                    with self.assertRaises(ValueError):
                        upload.get_timestamp()

    def test_rejects_environment_artifact_mismatch(self):
        (self.artifacts / "build-timestamp.txt").write_text("1789000123456")
        with patch.dict(os.environ, APP_BUILD_TIMESTAMP="2000000000000"):
            with self.assertRaisesRegex(ValueError, "differs"):
                upload.get_timestamp()


class MetadataTargetTest(unittest.TestCase):
    def setUp(self):
        self.enterContext(patch.dict(os.environ, {}, clear=True))

    def test_retains_existing_test_destination(self):
        with patch.object(upload, "test_version", True):
            self.assertEqual((46, "#updatetest"), upload.get_metadata_target())

    def test_stable_requires_explicit_destination(self):
        with patch.object(upload, "test_version", False):
            with self.assertRaisesRegex(ValueError, "required for #updatev2"):
                upload.get_metadata_target()

    def test_both_destinations_are_configurable(self):
        for is_test, tag in ((False, "#updatev2"), (True, "#updatetest")):
            with self.subTest(is_test=is_test), patch.object(upload, "test_version", is_test):
                with patch.dict(os.environ, UPDATE_METADATA_MESSAGE_ID="123"):
                    self.assertEqual((123, tag), upload.get_metadata_target())

    def test_rejects_invalid_destination(self):
        for message_id in ("0", "-1", "bad"):
            with self.subTest(message_id=message_id):
                with patch.dict(os.environ, UPDATE_METADATA_MESSAGE_ID=message_id):
                    with self.assertRaises(ValueError):
                        upload.get_metadata_target()


class PublishingTest(unittest.IsolatedAsyncioTestCase):
    def setUp(self):
        self.enterContext(patch.dict(os.environ, {}, clear=True))
        self.enterContext(patch.object(upload, "test_version", True))
        self.enterContext(patch.object(upload, "get_version", return_value=("12.9.0", 1241)))
        self.documents = self.enterContext(patch.object(upload, "get_document", return_value=[]))
        self.message = SimpleNamespace(text='#updatetest{"can_not_skip":false,"message":7}', edit=AsyncMock())
        self.messages = [SimpleNamespace(id=100, chat=SimpleNamespace(id=-123)), SimpleNamespace(id=101)]
        self.client = SimpleNamespace(
            send_media_group=AsyncMock(return_value=self.messages),
            forward_messages=AsyncMock(return_value=self.messages),
            get_messages=AsyncMock(return_value=self.message),
            start=AsyncMock(),
            stop=AsyncMock(),
        )

    async def test_retry_recovers_from_network_error(self):
        self.client.send_media_group.side_effect = [OSError("offline"), self.messages]
        self.assertIs(self.messages, await upload.send_to_channel(self.client, "-123"))
        self.assertEqual(2, self.client.send_media_group.await_count)

    async def test_all_network_stages_propagate_final_failure_without_logging_payload(self):
        for method, operation in (
            (self.client.send_media_group, lambda: upload.send_to_channel(self.client, "-123")),
            (self.client.forward_messages, lambda: upload.forward_to_channel(self.client, self.messages)),
            (self.client.get_messages, lambda: upload.edit_metadata_msg(self.client, self.messages, 123)),
            (self.message.edit, lambda: upload.edit_metadata_msg(self.client, self.messages, 123)),
        ):
            with self.subTest(method=method):
                method.reset_mock()
                failure = OSError("sensitive request data")
                method.side_effect = failure
                output = io.StringIO()
                with contextlib.redirect_stdout(output), contextlib.redirect_stderr(output):
                    with self.assertRaises(OSError) as raised:
                        await operation()
                self.assertIs(failure, raised.exception)
                self.assertEqual(3, method.await_count)
                self.assertEqual("", output.getvalue())
                method.side_effect = None

    async def test_metadata_tag_and_timestamp_match_publish_mode(self):
        for is_test, tag in ((True, "#updatetest"), (False, "#updatev2")):
            with self.subTest(is_test=is_test), patch.object(upload, "test_version", is_test):
                self.message.text = tag + '{"can_not_skip":false,"message":7}'
                with patch.dict(os.environ, UPDATE_METADATA_MESSAGE_ID="123"):
                    await upload.edit_metadata_msg(self.client, self.messages, 1789000123456)
                self.client.get_messages.assert_awaited_with(upload.metadata_channel, 123)
                text = self.message.edit.call_args.args[0]
                self.assertTrue(text.startswith(tag))
                data = json.loads(text[len(tag):])
                self.assertEqual(1789000123456, data["timestamp"])
                self.assertEqual(1241, data["version_code"])
                self.assertEqual(7, data["message"])
                self.assertEqual({"arm64-v8a": 100, "armeabi-v7a": 101}, data["gcm"])

    async def test_rejects_wrong_tag_or_malformed_metadata_without_editing(self):
        for text in (None, "#updatev2{}", "#updatetestbroken", "#updatetest[]", "#updatatest{}"):
            with self.subTest(text=text):
                self.message.text = text
                with self.assertRaises(ValueError):
                    await upload.edit_metadata_msg(self.client, self.messages, 123)
                self.message.edit.assert_not_awaited()

    async def test_main_stops_on_failure_and_closes_client(self):
        for failing_method in (self.client.send_media_group, self.client.forward_messages, self.message.edit):
            with self.subTest(method=failing_method):
                for method in (self.client.send_media_group, self.client.forward_messages, self.message.edit, self.client.stop):
                    method.reset_mock()
                failing_method.side_effect = OSError("offline")
                with patch.object(upload, "argv", ["upload.py", "mock-token", "-123", "test"]), patch.object(
                    upload, "get_timestamp", return_value=123
                ), patch.object(upload, "get_client", return_value=self.client):
                    with self.assertRaises(OSError):
                        await upload.main()
                self.client.stop.assert_awaited_once()
                if failing_method is self.client.send_media_group:
                    self.client.forward_messages.assert_not_awaited()
                if failing_method is not self.message.edit:
                    self.message.edit.assert_not_awaited()
                failing_method.side_effect = None

    async def test_missing_stable_configuration_fails_before_network(self):
        with patch.object(upload, "test_version", False), patch.object(
            upload, "get_timestamp", return_value=123
        ), patch.object(upload, "get_client") as get_client:
            with self.assertRaises(ValueError):
                await upload.main()
        get_client.assert_not_called()

    def test_client_session_is_not_persisted(self):
        with patch.object(upload, "Client", Mock()) as client:
            upload.get_client("mock-token")
        self.assertTrue(client.call_args.kwargs["in_memory"])


if __name__ == "__main__":
    unittest.main()
