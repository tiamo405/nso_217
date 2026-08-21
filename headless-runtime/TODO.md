# Headless Runtime TODO

- Add minimal `javax.microedition.midlet.MIDlet`. Done.
- Add no-op `javax.microedition.lcdui` classes used by the game. First pass done.
- Add file-backed `javax.microedition.rms.RecordStore`. Done.
- Add `javax.microedition.io.Connector` backed by `java.net.Socket` and `HttpURLConnection`. Done.
- Add a build script that compiles `headless-runtime/src`, then `src`, without `Microemulator.jar`. Done.
- After it compiles, test only one account first.

Khong dung folder nay de chay production cho den khi login va auto NVHN pass het cac buoc.
