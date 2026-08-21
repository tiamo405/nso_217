
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 *
 * @author cuong
 */
public class UpdateServer {

    public static int[] listPort;
    public static String[] listIP;
    public static int[] serverST;
    public static String[] listName;
    public static byte[] serverLoginList;
    public static String m;
    public static String url;

    public static boolean a() {
        // Use the server bundled with the client. This avoids depending on the
        // old remote server-list URL, which can make startup fail before login.
        return UpdateServer.b1();
    }
    
    public static boolean b1() {
        String decryptedM = decrypt(m);
        String[] var5 = a(decryptedM.trim(), new String(new char[]{','}), 0);// mã hóa
        UpdateServer.listName = new String[var5.length];
        UpdateServer.listIP = new String[var5.length];
        UpdateServer.listPort = new int[var5.length];
        UpdateServer.serverLoginList = new byte[var5.length];
        UpdateServer.serverST = new int[var5.length];

        for (int var6 = 0; var6 < var5.length; ++var6) {
            String[] var2 = a(var5[var6].trim(), new String(new char[]{':'}), 0);
            UpdateServer.listName[var6] = var2[0];
            UpdateServer.listIP[var6] = var2[1];
            UpdateServer.listPort[var6] = Integer.parseInt(var2[2]);
            UpdateServer.serverLoginList[var6] = Byte.parseByte(var2[3]);
            UpdateServer.serverST[var6] = var6;
        }

        c();
        return true;
    }

    public static void b() {
        try {
            ByteArrayInputStream var0 = new ByteArrayInputStream(RMS.gameAA(new String(new char[]{'a', 'b', 'c', 'd', 'i', 'p'})));
            DataInputStream var1 = new DataInputStream(var0);
            if (var0.available() > 0) {
                int var7 = var1.readInt();
                UpdateServer.listName = new String[var7];
                UpdateServer.listIP = new String[var7];
                UpdateServer.listPort = new int[var7];
                UpdateServer.serverLoginList = new byte[var7];
                UpdateServer.serverST = new int[var7];

                for (int var3 = 0; var3 < var7; ++var3) {
                    UpdateServer.listName[var3] = var1.readUTF();
                    UpdateServer.listIP[var3] = var1.readUTF();
                    UpdateServer.listPort[var3] = var1.readInt();
                    UpdateServer.serverLoginList[var3] = var1.readByte();
                    UpdateServer.serverST[var3] = var3;
                }

                var0.close();
                var1.close();
                return;
            }
        } catch (Exception var4) {
        }
        String decryptedM = decrypt(m);
        String[] var5 = a(decryptedM.trim(), new String(new char[]{','}), 0);// mã hóa
        
        //String[] var5 = a(m.trim(), new String(new char[]{','}), 0);
        UpdateServer.listName = new String[var5.length];
        UpdateServer.listIP = new String[var5.length];
        UpdateServer.listPort = new int[var5.length];
        UpdateServer.serverLoginList = new byte[var5.length];
        UpdateServer.serverST = new int[var5.length];

        for (int var6 = 0; var6 < var5.length; ++var6) {
            String[] var2 = a(var5[var6].trim(), new String(new char[]{':'}), 0);
            UpdateServer.listName[var6] = var2[0];
            UpdateServer.listIP[var6] = var2[1];
            UpdateServer.listPort[var6] = Integer.parseInt(var2[2]);
            UpdateServer.serverLoginList[var6] = Byte.parseByte(var2[3]);
            UpdateServer.serverST[var6] = var6;
        }

        c();
    }

    public static String encryptDecrypt(String input) {
        char key = 'K'; // Khóa bí mật (có thể thay đổi)
        StringBuffer output = new StringBuffer();

        for (int i = 0; i < input.length(); i++) {
            output.append((char) (input.charAt(i) ^ key));
        }

        return output.toString();
    }

    public static String[] a(String string, String string2, int n) {
        String[] stringArray;
        int n2 = string.indexOf(string2);
        if (n2 >= 0) {
            stringArray = UpdateServer.a(string.substring(n2 + string2.length()), string2, n + 1);
        } else {
            stringArray = new String[n + 1];
            n2 = string.length();
        }
        stringArray[n] = string.substring(0, n2);
        return stringArray;
    }

    public static String encrypt(String input) {
        return Base64Utils.encode(input.getBytes());
    }

    public static String decrypt(String input) {
        return new String(Base64Utils.decode(input));
    }

    public static void c() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            int n = UpdateServer.listIP.length;
            dataOutputStream.writeInt(n);
            int n2 = 0;
            while (n2 < n) {
                dataOutputStream.writeUTF(UpdateServer.listName[n2]);
                dataOutputStream.writeUTF(UpdateServer.listIP[n2]);
                dataOutputStream.writeInt(UpdateServer.listPort[n2]);
                dataOutputStream.writeByte(UpdateServer.serverLoginList[n2]);
                ++n2;
            }
            dataOutputStream.flush();
            byteArrayOutputStream.flush();
            RMS.gameAA(new String(new char[]{'a', 'b', 'c', 'd', 'i', 'p'}), byteArrayOutputStream.toByteArray());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    static {
        // Base64("NinjaSM:Nsm1.ninjasm.net:14444:0")
        m = "TmluamFTTTpOc20xLm5pbmphc20ubmV0OjE0NDQ0OjA=";
        url = "";
    }

}
