
import java.io.InputStream;
import javax.microedition.rms.RecordStore;

public final class RMS {

    public static InputStream fieldAA(String var0) {
        return "".getClass().getResourceAsStream(var0);
    }

    public static void gameAA(String var0, byte[] var1) {
        try {
            RecordStore var3;
            if ((var3 = RecordStore.openRecordStore("vj" + var0, true)).getNumRecords() > 0) {
                var3.setRecord(1, var1, 0, var1.length);
            } else {
                var3.addRecord(var1, 0, var1.length);
            }

            var3.closeRecordStore();
        } catch (Exception var2) {
        }
    }

    public static byte[] gameAA(String var0) {
        try {
            RecordStore var3;
            byte[] var1 = (var3 = RecordStore.openRecordStore("vj" + var0, false)).getRecord(1);
            var3.closeRecordStore();
            return var1;
        } catch (Exception var2) {
            return null;
        }
    }

    public static void gameAA(String var0, int var1) {
        try {
            gameAA(var0, new byte[]{(byte) var1});
        } catch (Exception var2) {
        }
    }

    public static void gameAA(String var0, String var1) {
        try {
            gameAA(var0, var1.getBytes("UTF-8"));
        } catch (Exception var2) {
            var2.printStackTrace();
        }
    }

    public static String loadRMSString(String var0) {
        byte[] var3;
        if ((var3 = gameAA(var0)) == null) {
            return null;
        } else {
            try {
                return new String(var3, "UTF-8");
            } catch (Exception var2) {
                return new String(var3);
            }
        }
    }

    public static int gameAC(String var0) {
        byte[] var1;
        return (var1 = gameAA(var0)) == null ? -1 : var1[0];
    }

    private static void gameAD(String var0) {
        try {
            RecordStore.deleteRecordStore("vj" + var0);
        } catch (Exception var1) {
        }
    }

    public static void gameAB(String var0, byte[] var1) {
        try {
            RecordStore var3;
            if ((var3 = RecordStore.openRecordStore("vj" + var0, true)).getNumRecords() > 0) {
                var3.setRecord(1, var1, 0, var1.length);
            } else {
                var3.addRecord(var1, 0, var1.length);
            }

            var3.closeRecordStore();
        } catch (Exception var2) {
            var2.printStackTrace();
        }
    }

    public static void gameAA() {
        gameAD("nj_arrow");
        gameAD("nj_effect");
        gameAD("nj_image");
        gameAD("nj_part");
        gameAD("nj_skill");
        gameAD("data");
        gameAD("dataVersion");
        gameAD("map");
        gameAD("mapVersion");
        gameAD("skill");
        gameAD("killVersion");
        gameAD("item");
        gameAD("itemVersion");
    }
}
