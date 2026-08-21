# Hướng Dẫn Sử Dụng NSO Python Client

## 📦 Cài Đặt

Không cần cài đặt package nào, chỉ cần Python 3:

```bash
python3 nso_client.py
```

## 🚀 Quick Start

### Ví dụ cơ bản

```python
from nso_client import NSOClient

# Tạo client
client = NSOClient("127.0.0.1", 14444)

# Kết nối
if client.connect():
    # Login
    client.login("username", "password")
    
    # Chọn nhân vật
    client.select_character(0)
    
    # Ngắt kết nối
    client.disconnect()
```

## 📚 Chi Tiết API

### Khởi tạo Client

```python
client = NSOClient(host, port)
```

**Parameters:**
- `host` (str): IP hoặc domain của game server
- `port` (int): Port của game server (thường là 14444)

### Kết nối

```python
success = client.connect()
```

**Returns:** `True` nếu kết nối thành công, `False` nếu thất bại

### Login

```python
success = client.login(username, password, version="217")
```

**Parameters:**
- `username` (str): Tên đăng nhập
- `password` (str): Mật khẩu
- `version` (str): Phiên bản game (mặc định "217")

**Returns:** `True` nếu gửi request thành công

### Chọn Nhân Vật

```python
success = client.select_character(char_index)
```

**Parameters:**
- `char_index` (int): Index của nhân vật (0, 1, 2...)

**Returns:** `True` nếu gửi request thành công

### Đăng Ký Tài Khoản

```python
success = client.register(username, password)
```

**Parameters:**
- `username` (str): Tên đăng nhập
- `password` (str): Mật khẩu

**Returns:** `True` nếu gửi request thành công

### Ngắt Kết Nối

```python
client.disconnect()
```

## 📋 Examples

Chạy file examples:

```bash
python3 examples.py
```

### Ví dụ 1: Login cơ bản

```python
client = NSOClient("game.server.com", 14444)

if client.connect():
    client.login("myuser", "mypass")
    client.disconnect()
```

### Ví dụ 2: Xử lý lỗi

```python
try:
    client = NSOClient("server.com", 14444)
    
    if not client.connect():
        print("Không thể kết nối")
        exit(1)
    
    if not client.login("user", "pass"):
        print("Login thất bại")
        exit(1)
    
    client.select_character(0)
    
except Exception as e:
    print(f"Lỗi: {e}")
finally:
    client.disconnect()
```

### Ví dụ 3: Auto retry

```python
max_retries = 3

for attempt in range(max_retries):
    client = NSOClient("server.com", 14444)
    
    if client.connect() and client.login("user", "pass"):
        print("Login thành công!")
        break
    
    client.disconnect()
    print(f"Thử lại {attempt + 1}/{max_retries}...")
    time.sleep(2)
```

## ⚠️ Lưu Ý

### Encryption

Client hiện tại chưa implement đầy đủ key exchange và encryption:

- Key exchange được gửi nhưng không parse key từ server
- Messages không được encrypt/decrypt
- Để hoàn chỉnh cần implement XOR encryption với key từ server

### Protocol Details

Xem `SOCKET_ENCRYPTION_ANALYSIS.md` để hiểu chi tiết về:
- Message format
- Command bytes
- Encryption algorithm

### Testing

Để test với real server:

1. Thay đổi `HOST` và `PORT` trong `nso_client.py`
2. Chạy `python3 nso_client.py`
3. Xem console output để debug

### Response Parsing

Client hiện tại chỉ parse response code cơ bản. Để parse đầy đủ response data cần:

1. Phân tích response structure từ Java code
2. Thêm parsing logic vào `_parse_login_response()`
3. Parse character list, game state, etc.

## 🔧 Mở Rộng

### Thêm Command Mới

Xem trong `Service.java` để tìm command bytes và format:

```python
def your_new_command(self, params):
    msg = NSOMessage(self.CMD_NOT_MAP)
    msg.write_byte(YOUR_COMMAND_BYTE)
    # Thêm parameters
    msg.write_int(param1)
    msg.write_utf(param2)
    
    self.send_message(msg)
    return self.receive_message()
```

### Implement Encryption

Để implement XOR encryption đầy đủ:

1. Parse key từ key exchange response
2. Lưu key vào `self.key`
3. Uncomment encryption code trong `send_message()`
4. Test với real server

## 📁 Files

```
nso_client.py   - Main client implementation
examples.py     - Usage examples
HOW_TO_CONNECT.md - Connection guide
SOCKET_ENCRYPTION_ANALYSIS.md - Protocol details
```

## 🐛 Troubleshooting

**Connection timeout:**
- Kiểm tra server có đang chạy không
- Kiểm tra firewall
- Thử với `telnet HOST PORT`

**Login failed:**
- Kiểm tra username/password đúng chưa
- Kiểm tra server có accept connection không
- Xem console log để debug

**No response:**
- Server có thể đã disconnect
- Timeout quá ngắn, tăng `socket.settimeout()`
- Check network connection
