public class CheckManager {

    public static final String[][] ipParts = {
        {"nsonew", "serivei", "com"},// Địa chỉ IP 1
        {"as"+"ad"+"ak", "aa", "aa", "as"}// Địa chỉ IP 2
    };

    public static String getIPAddress(String[] encodedParts) {
        StringBuffer ip = new StringBuffer();
        for (int i = 0; i < encodedParts.length; i++) {
            String decodedPart = decode(encodedParts[i]);
            ip.append(decodedPart);
            if (i < encodedParts.length - 1) {
                ip.append(".");
            }
        }
        return ip.toString();
    }

    private static String decode(String encodedPart) {
        StringBuffer result = new StringBuffer();
        if (encodedPart.charAt(0) != 'a') {
            return encodedPart;
        }
        for (int i = 0; i < encodedPart.length(); i += 2) {
            String part;
            if (i + 1 < encodedPart.length()) {
                part = "" + encodedPart.charAt(i) + encodedPart.charAt(i + 1);
            } else {
                part = "" + encodedPart.charAt(i);
            }
            if (part.equals("aa")) {
                result.append("0");
            } else if (part.equals("as")) {
                result.append("1");
            } else if (part.equals("ad")) {
                result.append("2");
            } else if (part.equals("af")) {
                result.append("3");
            } else if (part.equals("ag")) {
                result.append("4");
            } else if (part.equals("ah")) {
                result.append("5");
            } else if (part.equals("aj")) {
                result.append("6");
            } else if (part.equals("ak")) {
                result.append("7");
            } else if (part.equals("al")) {
                result.append("8");
            } else if (part.equals("a;")) {
                result.append("9");
            } else {
                result.append(part);
            }
        }
        return result.toString();
    }
}
