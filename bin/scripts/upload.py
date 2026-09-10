import contextlib
import json
import os
from functools import wraps
from pathlib import Path
from sys import argv
from typing import Iterable, Union

from pyrogram import Client, enums
from pyrogram.types import InputMediaDocument, Message
from release_caption import (
    is_changelog_ignored,
    read_apk_version,
    read_gradle_property,
    render_test_caption,
)

api_id = int(os.environ.get("API_ID", "11535358"))
api_hash = os.environ.get("API_HASH", "33d372962fadb01df47e6ceed4e33cd6")
metadata_channel = -1001471208507
artifacts_path = Path("artifacts")
test_version = len(argv) > 3 and argv[3] == "test"


def find_apk(abi: str) -> Path:
    dirs = list(artifacts_path.glob("*"))
    for dir in dirs:
        if dir.is_dir():
            apks = list(dir.glob("*.apk"))
            for apk in apks:
                if abi in apk.name:
                    return apk


def get_thumb() -> str:
    return "TMessagesProj/src/main/" + "ic_launcher-playstore.png"


def get_caption() -> str:
    with open(artifacts_path / "caption.txt", "r", encoding="utf-8") as f:
        commit_message = f.read()
    if not test_version:
        return "" if is_changelog_ignored(commit_message) else commit_message
    version_name, version_code = get_version()
    repository_url = "/".join(
        part.strip("/")
        for part in (
            os.environ.get("GITHUB_SERVER_URL", ""),
            os.environ.get("GITHUB_REPOSITORY", ""),
        )
        if part
    )
    return render_test_caption(
        commit_message,
        version_name,
        str(version_code),
        os.environ.get("GITHUB_SHA", ""),
        repository_url,
    )


def get_document() -> list["InputMediaDocument"]:
    documents = []
    abis = ["arm64-v8a", "armeabi-v7a"]
    for abi in abis:
        if apk := find_apk(abi):
            documents.append(
                InputMediaDocument(
                    media=str(apk),
                    thumb=get_thumb(),
                )
            )
    documents[-1].caption = get_caption()
    if test_version:
        documents[-1].parse_mode = enums.ParseMode.HTML
    return documents


def get_timestamp() -> int:
    timestamp_file = artifacts_path / "build-timestamp.txt"
    env_timestamp = os.environ.get("APP_BUILD_TIMESTAMP")
    if timestamp_file.exists():
        timestamp = int(timestamp_file.read_text(encoding="utf-8").strip())
        if env_timestamp and timestamp != int(env_timestamp):
            raise ValueError("APP_BUILD_TIMESTAMP differs from the build artifact")
    else:
        timestamp = int(env_timestamp or read_gradle_property("APP_BUILD_TIMESTAMP"))
    if timestamp <= 0:
        raise ValueError("Build timestamp must be positive")
    return timestamp


def get_metadata_target() -> tuple[int, str]:
    message_id = os.environ.get("UPDATE_METADATA_MESSAGE_ID")
    if not message_id:
        if not test_version:
            raise ValueError("UPDATE_METADATA_MESSAGE_ID is required for #updatev2")
        message_id = "46"
    message_id = int(message_id)
    if message_id <= 0:
        raise ValueError("UPDATE_METADATA_MESSAGE_ID must be positive")
    return message_id, "#updatetest" if test_version else "#updatev2"


def get_version() -> tuple[str, int]:
    apk = find_apk("arm64-v8a") or find_apk("armeabi-v7a")
    if apk is None:
        raise FileNotFoundError("No supported APK found")
    return read_apk_version(apk)


def retry(func):
    @wraps(func)
    async def wrapper(*args, **kwargs):
        for attempt in range(3):
            try:
                return await func(*args, **kwargs)
            except Exception:
                if attempt == 2:
                    raise

    return wrapper


@retry
async def send_to_channel(client: "Client", cid: str):
    with contextlib.suppress(ValueError):
        cid = int(cid)
    return await client.send_media_group(
        cid,
        media=get_document(),
    )


@retry
async def forward_to_channel(client: "Client", msg: list["Message"]):
    cid = msg[0].chat.id
    msg_ids = [m.id for m in msg]
    return await client.forward_messages(
        metadata_channel,
        cid,
        msg_ids,
    )


@retry
async def edit_metadata_msg(
    client: "Client", msg: Union["Message", Iterable["Message"]], timestamp: int
):
    message_id, tag = get_metadata_target()
    message = await client.get_messages(metadata_channel, message_id)
    if not message.text or not message.text.startswith(tag):
        raise ValueError("Metadata message does not match the publishing channel tag")
    json_dict = json.loads(message.text[len(tag):].strip())
    if not isinstance(json_dict, dict):
        raise ValueError("Metadata must be a JSON object")
    version_name, version_code = get_version()
    abis = ["gcm", "nogcm"]
    if not isinstance(msg, list):
        v8a, v7a = msg.id, msg.id
    elif len(msg) == 1:
        v8a, v7a = msg[0].id, msg[0].id
    else:
        v8a, v7a = msg[0].id, msg[1].id
    for abi in abis:
        json_dict[abi] = {"armeabi-v7a": v7a, "arm64-v8a": v8a}
    json_dict["version"] = f"{version_name} ({version_code})"
    json_dict["version_code"] = version_code
    json_dict["timestamp"] = timestamp
    json_text = json.dumps(json_dict)
    await message.edit(f"{tag}{json_text}")


def get_client(bot_token: str):
    return Client(
        "helper_bot",
        api_id=api_id,
        api_hash=api_hash,
        bot_token=bot_token,
        in_memory=True,
    )


async def main():
    timestamp = get_timestamp()
    get_metadata_target()
    bot_token = argv[1]
    chat_id = argv[2]
    client = get_client(bot_token)
    await client.start()
    try:
        msg = await send_to_channel(client, chat_id)
        msg = await forward_to_channel(client, msg)
        await edit_metadata_msg(client, msg, timestamp)
    finally:
        await client.stop()


if __name__ == "__main__":
    from asyncio import run

    try:
        run(main())
    except Exception as error:
        # RPC exception text can contain credentials or request data.
        from sys import stderr

        print(f"Publishing failed ({type(error).__name__}).", file=stderr)
        raise SystemExit(1) from None
