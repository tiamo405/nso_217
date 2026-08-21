# Cách Connect Đến NSO Game Server

## Câu Hỏi: Có thể dùng curl để connect đến game server không?

**Trả lời: KHÔNG thể dùng curl trực tiếp**

## Tại Sao?

NSO server sử dụng **TCP socket với binary protocol**, KHÔNG phải HTTP:

```
Game Server NSO:  socket://IP:14444  (TCP binary)
curl:             HTTP/HTTPS          (Web protocol)
                  ↑ Không tương thích!
```

## Các Cách Connect Đến Server Game

### Option 1: Dùng JAR Client (Recommended)

File `dist/client_217.jar` đã được build sẵn:

```bash
# Test với KEmulator
java -jar kemulator.jar dist/client_217.jar

# Hoặc J2ME emulator khác
```

JAR client đã implement đầy đủ protocol để connect đến server.

### Option 2: Dùng Python Script

File `connect_to_game_server.py` là ví dụ đơn giản:

```bash
python3 connect_to_game_server.py
```

**Thay đổi host/port trong file theo server thực tế của bạn:**

```python
HOST = "your-server.com"  # Thay bằng domain server game
PORT = 14444              # Thay bằng port server game
```

## Chi Tiết Protocol

**Xem file:** `SOCKET_ENCRYPTION_ANALYSIS.md`

- Socket connection: `socket://IP:PORT`
- Message format: Binary (DataOutputStream/DataInputStream)
- Encryption: XOR key-based sau khi exchange key
- Login flow: Connect → Key exchange → Send login message

## Tóm Tắt

| Tool | Có dùng được? | Ghi chú |
|------|---------------|---------|
| curl | ❌ KHÔNG | curl dùng cho HTTP, server dùng TCP binary |
| JAR client | ✅ CÓ | Cách đơn giản nhất, đã implement đầy đủ |
| Python script | ✅ CÓ | Cần implement protocol, xem file mẫu |
| netcat (nc) | ⚠️ CÓ nhưng khó | Phải gửi raw bytes thủ công |

**Kết luận:** Không thể dùng curl. Dùng JAR client hoặc viết script Python/Java để connect qua TCP socket.
