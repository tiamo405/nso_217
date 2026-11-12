import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * Thêm "HH:mm:ss " vào đầu mỗi dòng log để web control hiển thị đúng giờ của
 * từng dòng. Chỉ bật khi chạy với -Dnso.log.timestamp=true; các parser log
 * (supervise-workers.sh, status_workers_json.py, web_control/logparse.py) đều
 * chấp nhận prefix này là tùy chọn.
 */
public final class TimestampPrintStream extends PrintStream {
    private final OutputStream target;
    private boolean atLineStart = true;

    private TimestampPrintStream(OutputStream target) throws UnsupportedEncodingException {
        super(target, true, "UTF-8");
        this.target = target;
    }

    public static void install() {
        if (!"true".equalsIgnoreCase(System.getProperty("nso.log.timestamp"))) {
            return;
        }
        try {
            System.setOut(new TimestampPrintStream(System.out));
            System.setErr(new TimestampPrintStream(System.err));
        } catch (UnsupportedEncodingException ignored) {
            // UTF-8 luôn có trên JVM; nếu không thì giữ stream gốc.
        }
    }

    private void writePrefix() {
        long now = System.currentTimeMillis();
        long local = now + java.util.TimeZone.getDefault().getOffset(now);
        long secondsOfDay = ((local / 1000L) % 86400L + 86400L) % 86400L;
        int hour = (int)(secondsOfDay / 3600L);
        int minute = (int)(secondsOfDay / 60L % 60L);
        int second = (int)(secondsOfDay % 60L);
        byte[] prefix = new byte[] {
            (byte)('0' + hour / 10), (byte)('0' + hour % 10), ':',
            (byte)('0' + minute / 10), (byte)('0' + minute % 10), ':',
            (byte)('0' + second / 10), (byte)('0' + second % 10), ' '
        };
        try {
            this.target.write(prefix);
        } catch (java.io.IOException e) {
            this.setError();
        }
    }

    @Override
    public synchronized void write(int value) {
        if (this.atLineStart && value != '\n') {
            this.writePrefix();
            this.atLineStart = false;
        }
        try {
            this.target.write(value);
            if (value == '\n') {
                this.atLineStart = true;
                this.target.flush();
            }
        } catch (java.io.IOException e) {
            this.setError();
        }
    }

    @Override
    public synchronized void write(byte[] buffer, int offset, int length) {
        int end = offset + length;
        int start = offset;
        try {
            for (int index = offset; index < end; ++index) {
                if (this.atLineStart && buffer[index] != '\n') {
                    if (index > start) {
                        this.target.write(buffer, start, index - start);
                    }
                    start = index;
                    this.writePrefix();
                    this.atLineStart = false;
                }
                if (buffer[index] == '\n') {
                    this.target.write(buffer, start, index - start + 1);
                    start = index + 1;
                    this.atLineStart = true;
                }
            }
            if (start < end) {
                this.target.write(buffer, start, end - start);
            }
            this.target.flush();
        } catch (java.io.IOException e) {
            this.setError();
        }
    }

    @Override
    public synchronized void flush() {
        try {
            this.target.flush();
        } catch (java.io.IOException e) {
            this.setError();
        }
    }
}
