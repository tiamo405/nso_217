# NSO Headless Runtime Experiment

Thu muc nay la huong thu nghiem de chay bot khong qua MicroEmulator. Code cu va cac worker hien tai khong phu thuoc vao thu muc nay.

## Ket luan ngan

Khong the bo MicroEmulator va chay thang `GameMidlet` ngay lap tuc, vi client hien la J2ME MIDlet. MicroEmulator dang cung cap cac API ma JVM thuong khong co:

- `javax.microedition.midlet.MIDlet`
- `javax.microedition.lcdui.Canvas`, `Display`, `Graphics`, `Image`, `TextBox`
- `javax.microedition.io.Connector`, `SocketConnection`, `HttpConnection`
- `javax.microedition.rms.RecordStore`

Muon tiet kiem manh hon MicroEmulator thi can viet mot compatibility layer cuc nho, chi fake nhung API tren du cho bot auto chay.

## Huong lam

1. Compile source game voi stub `javax.microedition.*` trong thu muc nay.
2. Entry point la `HeadlessMain`, goi `new GameMidlet().startApp()`.
3. `Display`, `Canvas`, `Graphics`, `Image` khong ve that; chi tra kich thuoc va bo qua lenh paint.
4. `RecordStore` luu file rieng theo `-Duser.home`, giong cach worker hien tai tach RMS.
5. `Connector.open("socket://host:port")` map sang `java.net.Socket`.
6. Khi chay on dinh moi tinh tiep chuyen worker tu MicroEmulator sang runtime nay.

## Rui ro

Day la huong giam RAM/CPU lon nhat, nhung khong phai sua mot script la xong. Client bi dinh UI kha sau, nen can di tung lop stub, compile den dau sua den do, roi test login, chon nhan vat, NVHN, mua item, vao hang.

Huong it rui ro hon de test ngay la giam heap/GC/nice trong `scripts/start-workers.sh`.

## Lenh hien co

Compile rieng, khong tao JAR worker:

```bash
headless-runtime/build-headless.sh
```

Chay thu mot runtime:

```bash
HEADLESS_HOME=headless-runtime/run/worker-01 \
JAVA_XMX=48m \
headless-runtime/run-one.sh
```

Lenh chay thu se dung `account.csv` o repo root da duoc copy vao `build/classes`. Dung cho test nho truoc, chua thay the production worker.
