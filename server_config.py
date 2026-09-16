"""Server choices shared by the Ubuntu and Windows web controls."""

from __future__ import annotations


DEFAULT_SERVER = "tk"
SERVER_LABELS = {
    "ninjamobile": "NinjaMobile",
    "tk": "TK (Truyền Kỳ)",
}


def normalize_server(value: str | None) -> str:
    """Return the canonical server key used by the Java runtime."""
    key = (value or "").strip().lower()
    aliases = {
        "ninja": "ninjamobile",
        "ninja mobile": "ninjamobile",
        "truyenky": "tk",
        "truyền kỳ": "tk",
        "truyen ky": "tk",
    }
    key = aliases.get(key, key)
    if key not in SERVER_LABELS:
        raise ValueError("Server phải là ninjamobile hoặc tk")
    return key
