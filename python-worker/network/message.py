"""
NSO Binary Message Protocol Implementation (DataInputStream / DataOutputStream)
"""
import struct
from io import BytesIO


class MessageReader:
    def __init__(self, data: bytes):
        self.stream = BytesIO(data)

    def read_byte(self) -> int:
        b = self.stream.read(1)
        if not b:
            raise EOFError("End of message stream")
        return struct.unpack('b', b)[0]

    def read_unsigned_byte(self) -> int:
        b = self.stream.read(1)
        if not b:
            raise EOFError("End of message stream")
        return struct.unpack('B', b)[0]

    def read_boolean(self) -> bool:
        return self.read_unsigned_byte() != 0

    def read_short(self) -> int:
        b = self.stream.read(2)
        if len(b) < 2:
            raise EOFError("End of message stream")
        return struct.unpack('>h', b)[0]

    def read_unsigned_short(self) -> int:
        b = self.stream.read(2)
        if len(b) < 2:
            raise EOFError("End of message stream")
        return struct.unpack('>H', b)[0]

    def read_int(self) -> int:
        b = self.stream.read(4)
        if len(b) < 4:
            raise EOFError("End of message stream")
        return struct.unpack('>i', b)[0]

    def read_long(self) -> int:
        b = self.stream.read(8)
        if len(b) < 8:
            raise EOFError("End of message stream")
        return struct.unpack('>q', b)[0]

    def read_utf(self) -> str:
        length = self.read_unsigned_short()
        if length == 0:
            return ""
        data = self.stream.read(length)
        if len(data) < length:
            raise EOFError("Incomplete UTF string in message stream")
        return data.decode('utf-8', errors='replace')

    def read_bytes(self, length: int) -> bytes:
        data = self.stream.read(length)
        if len(data) < length:
            raise EOFError("Not enough bytes in message stream")
        return data

    def available(self) -> int:
        pos = self.stream.tell()
        self.stream.seek(0, 2)
        end = self.stream.tell()
        self.stream.seek(pos)
        return end - pos


class MessageWriter:
    def __init__(self):
        self.stream = BytesIO()

    def write_byte(self, v: int):
        self.stream.write(struct.pack('b', v))

    def write_unsigned_byte(self, v: int):
        self.stream.write(struct.pack('B', v))

    def write_boolean(self, v: bool):
        self.stream.write(struct.pack('B', 1 if v else 0))

    def write_short(self, v: int):
        self.stream.write(struct.pack('>h', v))

    def write_unsigned_short(self, v: int):
        self.stream.write(struct.pack('>H', v))

    def write_int(self, v: int):
        self.stream.write(struct.pack('>i', v))

    def write_long(self, v: int):
        self.stream.write(struct.pack('>q', v))

    def write_utf(self, text: str):
        b = text.encode('utf-8')
        self.write_unsigned_short(len(b))
        self.stream.write(b)

    def write_bytes(self, data: bytes):
        self.stream.write(data)

    def get_data(self) -> bytes:
        return self.stream.getvalue()


class NSOMessage:
    def __init__(self, command: int, data: bytes = None):
        self.command = command
        self._writer = MessageWriter()
        if data is not None:
            self._reader = MessageReader(data)
            self._data = data
        else:
            self._reader = None
            self._data = None

    def writer(self) -> MessageWriter:
        return self._writer

    def reader(self) -> MessageReader:
        if self._reader is None:
            self._reader = MessageReader(self.get_data())
        return self._reader

    def get_data(self) -> bytes:
        if self._data is not None:
            return self._data
        return self._writer.get_data()

    def __repr__(self):
        data = self.get_data()
        return f"<NSOMessage cmd={self.command} len={len(data)}>"

