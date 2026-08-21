# Phân Tích Socket Connection và Encryption - NSO Client 217

## 📡 Socket Connection Details

### 1. Server URL Configuration

**File**: `UpdateServer.java`

#### URL được mã hóa Base64:
```java
static {
    m = "aHR0cDovL25zbzl4Lm9ubGluZS9zZXJ2ZXJzL05TT185WC50eHQ=";
    url = "aHR0cDovL25zbzl4Lm9ubGluZS9zZXJ2ZXJzL05TT185WC50eHQ=";
}
```

#### URL sau khi decrypt:
```
http://nso9x.online/servers/NSO_9X.txt
```

**Cách decrypt:**
```bash
echo "aHR0cDovL25zbzl4Lm9ubGluZS9zZXJ2ZXJzL05TT185WC50eHQ=" | base64 -d
```

### 2. Server List Format

File `NSO_9X.txt` trên server chứa danh sách server theo format:

```
ServerName1:IP1:Port1:LoginByte1,ServerName2:IP2:Port2:LoginByte2,...
```

**Ví dụ:**
```
NsoBun:nsonew.serivei.com:14444:0,Server2:192.168.1.100:14445:0
```

**Parse code** (UpdateServer.java:73-88):
```java
String[] stringArray = UpdateServer.a(string.trim(), ",", 0);
UpdateServer.listName[n] = stringArray2[0];      // Server name
UpdateServer.listIP[n] = stringArray2[1];        // IP/hostname
UpdateServer.listPort[n] = Integer.parseInt(stringArray2[2]); // Port
UpdateServer.serverLoginList[n] = Byte.parseByte(stringArray2[3]); // Login byte
```

### 3. Socket Connection Process

**File**: `Session_ME.java`

#### Socket URL Format (line 166):
```java
String var3 = "socket://" + var1 + ":" + var2;

// Thêm parameters tùy theo kết nối:
// WiFi: ";interface=wifi"
// GPRS: ";deviceside=true"
```

**Ví dụ socket URL:**
```
socket://nsonew.serivei.com:14444
socket://192.168.1.100:14444;interface=wifi
socket://game.server.com:14444;deviceside=true
```

#### Connection Flow (Session_ME.java:292-326):
```java
// 1. Mở socket connection
this.gameAB.fieldAE = (SocketConnection) Connector.open(socketUrl);

// 2. Mở output stream
Session_ME.gameAA(this.gameAB, this.gameAB.fieldAE.openDataOutputStream());

// 3. Mở input stream
this.gameAB.dis = this.gameAB.fieldAE.openDataInputStream();

// 4. Start message sender thread
(new Thread(Session_ME.gameAA(this.gameAB))).start();

// 5. Start message receiver thread
this.gameAB.gameAI = new Thread(new MessageCollector(this.gameAB));
this.gameAB.gameAI.start();

// 6. Gửi initial message để request encryption key
Session_ME.gameAA(this.gameAB, new Message((byte) -27));
```

### 4. Cách Thay Đổi Server

#### Option 1: Thay đổi URL trong code
Edit `UpdateServer.java` line 221:
```java
static {
    // Encode URL mới sang Base64
    m = "YOUR_BASE64_ENCODED_URL_HERE";
}
```

**Tạo Base64 URL:**
```bash
echo -n "http://your-server.com/servers/list.txt" | base64
```

#### Option 2: Manual Input Server
Sử dụng method `b1()` để input server list trực tiếp:
```java
UpdateServer.m = encrypt("ServerName:IP:Port:0");
UpdateServer.b1();
```

---

## 🔐 Encryption System

### 1. Encryption Overview

NSO Client sử dụng **2 loại encryption**:

1. **Base64 Encoding** - Cho URL và config strings
2. **XOR Key-based Encryption** - Cho network messages

### 2. Base64 Encryption

**File**: `UpdateServer.java:187-193`

```java
public static String encrypt(String input) {
    return Base64Utils.encode(input.getBytes());
}

public static String decrypt(String input) {
    return new String(Base64Utils.decode(input));
}
```

**Sử dụng:**
- Encrypt/decrypt URL
- Encrypt/decrypt server list string
- Simple obfuscation, không phải security encryption

**Ví dụ:**
```java
String original = "http://nso9x.online/servers/NSO_9X.txt";
String encrypted = Base64Utils.encode(original.getBytes());
// Result: "aHR0cDovL25zbzl4Lm9ubGluZS9zZXJ2ZXJzL05TT185WC50eHQ="

String decrypted = new String(Base64Utils.decode(encrypted));
// Result: "http://nso9x.online/servers/NSO_9X.txt"
```

### 3. XOR Key-Based Encryption (Network Messages)

**File**: `Session_ME.java`

#### Key Storage (line 24):
```java
public byte[] key = null;  // Encryption key array
private byte curR;         // Current read position in key
private byte curW;         // Current write position in key
```

#### Encryption Method (Session_ME.java:329-339):
```java
private byte gameAA(byte var1) {
    byte[] var10000 = this.key;
    byte var10003 = this.curW;
    this.curW = (byte) (var10003 + 1);
    var1 = (byte) (var10000[var10003] & 255 ^ var1 & 255);
    
    // Wrap around khi hết key array
    if (this.curW >= this.key.length) {
        this.curW = (byte) (this.curW % this.key.length);
    }
    
    return var1;
}
```

**Algorithm: Rolling XOR**
```
encrypted_byte = key[position] XOR original_byte
position = (position + 1) % key.length
```

#### Message Encryption Process (Session_ME.java:217-257):

**Khi gửi message:**
```java
1. Check if key exchange complete
   if (this.getKeyComplete) {
       // Encrypt command byte
       byte encryptedCmd = this.gameAA(var1.command);
       this.dos.writeByte(encryptedCmd);
   }

2. Encrypt message length (2 bytes)
   byte encryptedLenHigh = this.gameAA((byte) (length >> 8));
   byte encryptedLenLow = this.gameAA((byte) length);

3. Encrypt message data
   for (int i = 0; i < data.length; ++i) {
       data[i] = this.gameAA(data[i]);
   }
```

**Structure của encrypted message:**
```
[1 byte] Encrypted Command
[2 bytes] Encrypted Length (high byte, low byte)
[n bytes] Encrypted Data
```

#### Decryption Method (Session_ME.java:401-411):
```java
static byte gameAA(Session_ME var0, byte var1) {
    byte[] var10000 = var0.key;
    byte var10003 = var0.curR;
    var0.curR = (byte) (var10003 + 1);
    var1 = (byte) (var10000[var10003] & 255 ^ var1 & 255);
    
    if (var0.curR >= var0.key.length) {
        var0.curR = (byte) (var0.curR % var0.key.length);
    }
    
    return var1;
}
```

**Decryption process tương tự encryption vì XOR symmetric:**
```
decrypted_byte = key[position] XOR encrypted_byte
position = (position + 1) % key.length
```

### 4. Key Exchange Protocol

**Initial Connection (Session_ME.java:50-59):**
```java
// 1. Connect without key
this.getKeyComplete = false;

// 2. Send key request message
Message keyRequest = new Message((byte) -27);
session.sendMessage(keyRequest);

// 3. Server responds with key
// 4. Client stores key
this.key = receivedKey;
this.getKeyComplete = true;

// 5. All subsequent messages encrypted with key
```

**Key Properties:**
- Key is byte array
- Length varies (server determines)
- Shared between client and server
- Used as circular buffer for XOR operations

### 5. XOR Encryption/Decryption Tool

**Standalone decrypt/encrypt code:**

```java
public class MessageDecryptor {
    private byte[] key;
    private int position = 0;
    
    public MessageDecryptor(byte[] key) {
        this.key = key;
    }
    
    public byte[] decrypt(byte[] encrypted) {
        byte[] decrypted = new byte[encrypted.length];
        for (int i = 0; i < encrypted.length; i++) {
            decrypted[i] = (byte) (key[position] & 0xFF ^ encrypted[i] & 0xFF);
            position = (position + 1) % key.length;
        }
        return decrypted;
    }
    
    public byte[] encrypt(byte[] data) {
        // XOR is symmetric, same operation
        return decrypt(data);
    }
    
    public void resetPosition() {
        position = 0;
    }
}
```

**Usage example:**
```java
// Assume key received from server
byte[] key = new byte[]{0x4A, 0x7B, 0x9C, 0x2F, ...};

// Decrypt received message
MessageDecryptor dec = new MessageDecryptor(key);
byte[] encrypted = receivedBytes;
byte[] decrypted = dec.decrypt(encrypted);

// Encrypt message to send
MessageDecryptor enc = new MessageDecryptor(key);
byte[] plain = myData;
byte[] encrypted = enc.encrypt(plain);
```

---

## 🔍 Message Structure

### Message Class (Message.java)

```java
public class Message {
    public byte command;                    // Command byte
    private ByteArrayOutputStream bos;      // For writing
    private DataOutputStream dos;
    private ByteArrayInputStream bis;       // For reading
    private DataInputStream dis;
}
```

### Creating Messages

```java
// Create message for sending
Message msg = new Message((byte) -127);  // -127 = Login command
msg.writer().writeUTF(username);
msg.writer().writeUTF(password);
session.sendMessage(msg);
msg.cleanup();
```

### Reading Messages

```java
// Parse received message
Message msg = new Message(command, data);
String response = msg.reader().readUTF();
int value = msg.reader().readInt();
```

---

## 🛠️ Debugging Network Traffic

### 1. Log Messages

**Add logging to Session_ME.java:**

```java
// In sendMessage method (line 217):
private synchronized void gameAB(Message var1) {
    System.out.println("[SEND] Command: " + var1.command + " Size: " + var1.getData().length);
    // ... existing code
}

// In receive method (MessageCollector class):
System.out.println("[RECV] Command: " + command + " Size: " + length);
```

### 2. Decrypt Traffic

**Để decrypt captured traffic:**

1. Capture key exchange message (command -27)
2. Extract key từ server response
3. Use MessageDecryptor với key đó
4. Decrypt subsequent messages

### 3. Monitor Connection

```java
// Check connection status
boolean isConnected = Session_ME.gI().connected;
boolean isConnecting = Session_ME.gI().connecting;

// Bytes transferred
int sentBytes = Session_ME.gI().sendByteCount;
int recvBytes = Session_ME.gI().recvByteCount;
```

---

## 📊 Command Bytes Reference

### Common Client Commands

```java
-127  // Login
-126  // Select character
-125  // Set client type
-30   // Sub-commands
-29   // Not login commands
-28   // Not map commands
-27   // Key exchange request
-31   // Special command (no length encryption)
```

### Parsing in Controller.java

```java
// Controller receives messages and dispatches by command byte
public void onMessage(Message msg) {
    byte cmd = msg.command;
    switch(cmd) {
        case -127:
            handleLogin(msg);
            break;
        case -126:
            handleCharSelect(msg);
            break;
        // ... more handlers
    }
}
```

---

## 🔧 Thay Đổi Encryption

### Disable Encryption (For Testing)

**Session_ME.java:**
```java
// Comment out encryption in gameAB method
private synchronized void gameAB(Message var1) {
    byte[] var2 = var1.getData();
    try {
        // Always send unencrypted (comment out key logic)
        this.dos.writeByte(var1.command);  // Direct write, no encryption
        
        if (var2 != null) {
            this.dos.writeShort(var2.length);  // Direct length
            this.dos.write(var2);              // Direct data
        } else {
            this.dos.writeShort(0);
        }
        
        this.dos.flush();
    } catch (IOException var4) {
        var4.printStackTrace();
    }
}
```

**⚠️ Warning:** Server phải cũng disable encryption để test!

### Change Encryption Algorithm

Thay thế XOR với algorithm khác:

```java
// Example: Use simple Caesar cipher
private byte encryptByte(byte input) {
    return (byte) ((input + 5) % 256);  // Shift by 5
}

private byte decryptByte(byte input) {
    return (byte) ((input - 5 + 256) % 256);
}
```

---

## 📝 Summary

### Socket Connection:
1. ✅ URL: `http://nso9x.online/servers/NSO_9X.txt` (Base64 encoded in code)
2. ✅ Format: `socket://IP:PORT` with optional parameters
3. ✅ Connection: J2ME SocketConnection via Connector.open()

### Encryption:
1. ✅ **Base64** - URL và strings (simple obfuscation)
2. ✅ **XOR with key array** - Network messages (rolling cipher)
3. ✅ Key exchange via command -27 sau khi connect
4. ✅ Symmetric encryption (XOR works both ways)

### Decryption:
1. ✅ Base64: `new String(Base64Utils.decode(encrypted))`
2. ✅ XOR: `decrypted = key[pos] XOR encrypted[pos]`
3. ✅ Position wraps around: `pos = (pos + 1) % key.length`

### Tools Provided:
- Base64 encrypt/decrypt methods in UpdateServer.java
- XOR encrypt/decrypt methods in Session_ME.java
- MessageDecryptor class example for standalone use
