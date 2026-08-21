public class Base64Utils {

    private static final char[] CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    
    public static String encode(byte[] data) {
        StringBuffer encoded = new StringBuffer();
        int padding = 0;
        for (int i = 0; i < data.length; i += 3) {
            int chunk = ((data[i] & 0xFF) << 16) | ((i + 1 < data.length ? (data[i + 1] & 0xFF) << 8 : 0)) | (i + 2 < data.length ? (data[i + 2] & 0xFF) : 0);
            
            encoded.append(CHARS[(chunk >> 18) & 0x3F]);
            encoded.append(CHARS[(chunk >> 12) & 0x3F]);
            
            if (i + 1 < data.length) {
                encoded.append(CHARS[(chunk >> 6) & 0x3F]);
            } else {
                encoded.append('=');
                padding++;
            }
            
            if (i + 2 < data.length) {
                encoded.append(CHARS[chunk & 0x3F]);
            } else {
                encoded.append('=');
                padding++;
            }
        }
        return encoded.toString();
    }

    public static byte[] decode(String input) {
        int[] INDEX_TABLE = new int[128];
        for (int i = 0; i < CHARS.length; i++) {
            INDEX_TABLE[CHARS[i]] = i;
        }
        
        byte[] output = new byte[(input.length() * 3) / 4 - (input.endsWith("==") ? 2 : input.endsWith("=") ? 1 : 0)];
        int outIndex = 0;
        
        for (int i = 0; i < input.length(); i += 4) {
            int chunk = (INDEX_TABLE[input.charAt(i)] << 18) | (INDEX_TABLE[input.charAt(i + 1)] << 12)
                    | ((i + 2 < input.length() && input.charAt(i + 2) != '=') ? (INDEX_TABLE[input.charAt(i + 2)] << 6) : 0)
                    | ((i + 3 < input.length() && input.charAt(i + 3) != '=') ? INDEX_TABLE[input.charAt(i + 3)] : 0);
            
            output[outIndex++] = (byte) ((chunk >> 16) & 0xFF);
            if (i + 2 < input.length() && input.charAt(i + 2) != '=') {
                output[outIndex++] = (byte) ((chunk >> 8) & 0xFF);
            }
            if (i + 3 < input.length() && input.charAt(i + 3) != '=') {
                output[outIndex++] = (byte) (chunk & 0xFF);
            }
        }
        return output;
    }
}
