
import java.io.ByteArrayOutputStream;
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

        prioritizeSelectedServer();
        c();
        return true;
    }

    public static void b() {
        // Không đọc danh sách server cũ đã cache trong RMS ("abcdip") vì có
        // thể còn domain cũ; luôn dùng server đóng gói trong client.
        UpdateServer.b1();
    }

    /**
     * Chỉ còn một server game. Có thể ghi đè bằng "host:port" qua
     * -Dnso.server hoặc NSO_SERVER; giá trị cũ dạng tên server (tk,
     * ninjamobile...) bị bỏ qua và dùng server mặc định.
     */
    private static boolean applyCustomEndpoint() {
        String value = System.getProperty("nso.server");
        if (value == null || value.trim().length() == 0) {
            value = System.getenv("NSO_SERVER");
        }
        if (value == null) {
            return false;
        }
        value = value.trim();
        int colon = value.lastIndexOf(':');
        if (colon <= 0 || colon == value.length() - 1) {
            return false;
        }
        String host = value.substring(0, colon).trim();
        int port;
        try {
            port = Integer.parseInt(value.substring(colon + 1).trim());
        } catch (NumberFormatException ex) {
            return false;
        }
        if (host.length() == 0 || port <= 0 || port > 65535) {
            return false;
        }
        UpdateServer.listName[0] = "custom";
        UpdateServer.listIP[0] = host;
        UpdateServer.listPort[0] = port;
        UpdateServer.serverLoginList[0] = 0;
        return true;
    }

    private static void prioritizeSelectedServer() {
        if (UpdateServer.listName == null || UpdateServer.listIP == null
                || UpdateServer.listName.length == 0) {
            return;
        }
        applyCustomEndpoint();
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
        // Server duy nhất: Base64("NSO217:180.93.114.122:14444:0")
        m = "TlNPMjE3OjE4MC45My4xMTQuMTIyOjE0NDQ0OjA=";
        url = "";
    }

}
