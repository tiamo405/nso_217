"""
Configuration for NSO Python Headless Bot Worker
"""
import os

# Server connection default settings
DEFAULT_HOST = "Nsm1.ninjasm.net"
DEFAULT_PORT = 14444
DEFAULT_CLIENT_VERSION = "2.1.7"

# Socket & timing configurations
SOCKET_TIMEOUT = 12.0  # seconds for socket read/write timeout
CONNECT_TIMEOUT = 10.0
RECONNECT_MAX_RETRIES = 3
RECONNECT_DELAY = 5.0  # seconds

# Action delays (seconds)
ACTION_DELAY_SHORT = 0.3
ACTION_DELAY_NORMAL = 0.7
ACTION_DELAY_LONG = 1.2
ACTION_DELAY_MAP_CHANGE = 2.0

# Base directories
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CONFIG_DIR = os.path.join(BASE_DIR, "config")
DELETE_ITEMS_FILE = os.path.join(CONFIG_DIR, "delete_items.txt")

