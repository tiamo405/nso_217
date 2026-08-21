package javax.microedition.rms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public final class RecordStore {
    private final File file;

    private RecordStore(File file) {
        this.file = file;
    }

    public static RecordStore openRecordStore(String name, boolean createIfNecessary) throws IOException {
        File dir = new File(System.getProperty("user.home", "."), ".nso-headless-rms");
        if (!dir.isDirectory() && !dir.mkdirs()) {
            throw new IOException("Cannot create RMS dir: " + dir);
        }
        File file = new File(dir, sanitize(name) + ".rs");
        if (!file.exists()) {
            if (!createIfNecessary) {
                throw new IOException("RecordStore not found: " + name);
            }
            FileOutputStream out = new FileOutputStream(file);
            out.close();
        }
        return new RecordStore(file);
    }

    public static void deleteRecordStore(String name) {
        File dir = new File(System.getProperty("user.home", "."), ".nso-headless-rms");
        File file = new File(dir, sanitize(name) + ".rs");
        if (file.exists()) {
            file.delete();
        }
    }

    public int getNumRecords() {
        return file.length() > 0L ? 1 : 0;
    }

    public int addRecord(byte[] data, int offset, int numBytes) throws IOException {
        write(data, offset, numBytes);
        return 1;
    }

    public void setRecord(int recordId, byte[] data, int offset, int numBytes) throws IOException {
        write(data, offset, numBytes);
    }

    public byte[] getRecord(int recordId) throws IOException {
        FileInputStream in = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        int read = 0;
        while (read < data.length) {
            int count = in.read(data, read, data.length - read);
            if (count < 0) {
                break;
            }
            read += count;
        }
        in.close();
        return data;
    }

    public void closeRecordStore() {
    }

    private void write(byte[] data, int offset, int numBytes) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        out.write(data, offset, numBytes);
        out.close();
    }

    private static String sanitize(String name) {
        return name.replaceAll("[^A-Za-z0-9._-]", "_");
    }
}
