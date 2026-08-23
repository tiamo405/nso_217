"""
NSO Protocol Rolling XOR Encryption Implementation
Matches Session_ME.java & MessageCollector.java
"""

class RollingXOR:
    def __init__(self):
        self.key = None
        self.cur_r = 0
        self.cur_w = 0
        self.is_ready = False

    def setup_key(self, raw_key_bytes: bytes):
        """
        Setup key array with cumulative XOR as implemented in MessageCollector.java
        """
        key_list = bytearray(raw_key_bytes)
        for i in range(len(key_list) - 1):
            key_list[i + 1] ^= key_list[i]
        self.key = bytes(key_list)
        self.cur_r = 0
        self.cur_w = 0
        self.is_ready = True

    def reset(self):
        self.key = None
        self.cur_r = 0
        self.cur_w = 0
        self.is_ready = False

    def encrypt_byte(self, b: int) -> int:
        if not self.is_ready or not self.key:
            return b & 0xFF
        k = self.key[self.cur_w]
        self.cur_w = (self.cur_w + 1) % len(self.key)
        return (k ^ (b & 0xFF)) & 0xFF

    def decrypt_byte(self, b: int) -> int:
        if not self.is_ready or not self.key:
            return b & 0xFF
        k = self.key[self.cur_r]
        self.cur_r = (self.cur_r + 1) % len(self.key)
        return (k ^ (b & 0xFF)) & 0xFF

    def encrypt_data(self, data: bytes) -> bytes:
        if not self.is_ready or not self.key:
            return data
        res = bytearray(len(data))
        for i, b in enumerate(data):
            res[i] = self.encrypt_byte(b)
        return bytes(res)

    def decrypt_data(self, data: bytes) -> bytes:
        if not self.is_ready or not self.key:
            return data
        res = bytearray(len(data))
        for i, b in enumerate(data):
            res[i] = self.decrypt_byte(b)
        return bytes(res)

