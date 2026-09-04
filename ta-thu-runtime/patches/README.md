# Patches cho Ta Thu Runtime

Các patch này được áp dụng tự động trong `build-ta-thu.sh`.

## Controller_null_safety.patch
Bảo vệ `gameAG(Message)` khỏi NPE khi nhận null buffer.

**Vấn đề:** 
```
java.lang.NullPointerException: Cannot read the array length because "buf" is null
	at Controller.gameAG(Controller.java:4213)
```

**Giải pháp:** Thêm null check trước khi xử lý message.
