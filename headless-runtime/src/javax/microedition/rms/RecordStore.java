package javax.microedition.rms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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
            byte[] defaultData = defaultRecordData(name);
            if (defaultData != null) {
                writeFile(file, defaultData, 0, defaultData.length);
            } else if (!createIfNecessary) {
                throw new IOException("RecordStore not found: " + name);
            } else {
                FileOutputStream out = new FileOutputStream(file);
                out.close();
            }
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
        writeFile(file, data, offset, numBytes);
    }

    private static void writeFile(File file, byte[] data, int offset, int numBytes) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        out.write(data, offset, numBytes);
        out.close();
    }

    private static String sanitize(String name) {
        return name.replaceAll("[^A-Za-z0-9._-]", "_");
    }

    private static byte[] defaultRecordData(String name) throws IOException {
        if ("vjV6Group".equals(name)) {
            return defaultV6Group();
        }
        if ("vjV7ProSetting".equals(name)) {
            return defaultV7ProSetting();
        }
        return null;
    }

    private static byte[] defaultV6Group() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bytes);
        out.writeUTF("");
        out.writeByte(0);
        out.writeInt(0);
        out.flush();
        return bytes.toByteArray();
    }

    private static byte[] defaultV7ProSetting() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bytes);

        out.writeBoolean(false); // isAHP
        out.writeInt(20);        // aHpValue
        out.writeBoolean(false); // isAMP
        out.writeInt(20);        // aMpValue
        out.writeBoolean(false); // isAFood
        out.writeInt(70);        // aFoodValue
        out.writeBoolean(true);  // isABuff
        out.writeBoolean(false); // fieldEH
        out.writeBoolean(false); // fieldEI
        out.writeBoolean(true);  // fieldEG
        out.writeBoolean(true);  // isAPickYen
        out.writeBoolean(true);  // isAPickYHM
        out.writeInt(30);        // fieldFR
        out.writeBoolean(false); // isAPickYHMS
        out.writeInt(3);         // fieldFS
        out.writeBoolean(false); // fieldEM
        out.writeInt(5);         // fieldFT
        out.writeBoolean(false); // fieldEN
        out.writeBoolean(false); // fieldEO
        out.writeInt(30);        // fieldFU
        out.writeBoolean(false); // fieldEP
        out.writeBoolean(true);  // fieldEQ
        out.writeBoolean(false); // fieldER
        out.writeBoolean(false); // fieldES
        out.writeBoolean(false); // isANoPick
        out.writeBoolean(true);  // fieldEW
        out.writeBoolean(true);  // fieldEX
        out.writeBoolean(false); // fieldEY
        out.writeBoolean(true);  // fieldEZ
        out.writeBoolean(true);  // ReConnect
        out.writeBoolean(true);  // fieldFB
        out.writeBoolean(false); // fieldFJ
        out.writeBoolean(true);  // fieldFC
        out.writeBoolean(true);  // fieldFD
        out.writeBoolean(true);  // fieldFE
        out.writeBoolean(true);  // fieldFF
        out.writeBoolean(false); // fieldFG
        out.writeBoolean(false); // fieldFH
        out.writeBoolean(true);  // fieldFI

        out.writeInt(0);         // pick item count
        out.writeInt(30);        // Code.speedGame
        out.writeBoolean(false); // fieldEU
        out.writeBoolean(false); // fieldEV
        out.writeInt(0);         // delete item count
        out.writeInt(0);         // throw item count
        out.writeBoolean(false); // isUseCL
        out.writeInt(0);         // item buy count
        out.writeBoolean(false); // isBuyCL

        out.flush();
        return bytes.toByteArray();
    }
}
