"""Test NSO login for a single account.
Run this script from inside the `python-worker` directory:

    cd python-worker
    python3 test_login.py

Replace USERNAME / PASSWORD as needed, or set the password via the NSO_PASSWORD environment variable.
"""

import os
import sys
import time
import logging

# Ensure the current directory (python-worker) is on sys.path
sys.path.append(os.path.abspath(os.path.dirname(__file__)))

from network.socket_client import NSOSocketClient
from protocol.service import NSOService
from protocol.controller import NSOController
from config import config

logging.basicConfig(level=logging.INFO, format="%(asctime)s %(message)s")
logger = logging.getLogger("TestLogin")

# ------------------------------------------------------------
# Account information – change USERNAME if you want to test another account.
# Password can be set directly here or via the NSO_PASSWORD environment variable.
# ------------------------------------------------------------
USERNAME = "luongclone2251"  # <-- change if needed
PASSWORD = os.getenv("NSO_PASSWORD") or "ngan2021"

def main() -> None:
    client = NSOSocketClient(config.DEFAULT_HOST, config.DEFAULT_PORT, timeout=10.0)
    if not client.connect():
        logger.error("Cannot connect to server %s:%s", config.DEFAULT_HOST, config.DEFAULT_PORT)
        return

    controller = NSOController(service=NSOService(client))
    client.on_message_callback = controller.handle_message
    controller.reset_for_account()
    service = NSOService(client)
    logger.info("Sending login request for account %s", USERNAME)
    service.send_login(USERNAME, PASSWORD, config.DEFAULT_CLIENT_VERSION)
    time.sleep(5)
    if getattr(controller, "is_in_character_select", False) and getattr(controller, "character_list", None):
        logger.info("✅ Login successful – %d characters", len(controller.character_list))
    else:
        msg = getattr(controller, "last_server_message", "No response")
        logger.warning("⚠️ Login failed: %s", msg)
    client.disconnect()

if __name__ == "__main__":
    main()

