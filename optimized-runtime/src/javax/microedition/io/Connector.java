package javax.microedition.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public final class Connector {
    private Connector() {
    }

    public static Object open(String name) throws IOException {
        if (name == null) {
            throw new ConnectionNotFoundException("null connection");
        }
        if (name.startsWith("socket://")) {
            return openSocket(name.substring("socket://".length()));
        }
        if (name.startsWith("http://") || name.startsWith("https://")) {
            return new JavaHttpConnection(name);
        }
        if (name.startsWith("sms://")) {
            return new javax.wireless.messaging.NoopMessageConnection();
        }
        throw new ConnectionNotFoundException(name);
    }

    private static SocketConnection openSocket(String target) throws IOException {
        int split = target.lastIndexOf(':');
        if (split <= 0 || split == target.length() - 1) {
            throw new ConnectionNotFoundException("Invalid socket target: " + target);
        }
        String host = target.substring(0, split);
        int port = Integer.parseInt(target.substring(split + 1));
        String proxy = System.getenv("NSO_SOCKS5_PROXY");
        if (proxy == null || proxy.trim().length() == 0) {
            if ("1".equals(System.getenv("NSO_PROXY_REQUIRED"))) {
                throw new IOException("SOCKS5 proxy is required but missing");
            }
            return new JavaSocketConnection(new Socket(host, port));
        }
        return new JavaSocketConnection(openSocks5Socket(host, port, proxy));
    }

    private static Socket openSocks5Socket(String targetHost, int targetPort, String proxy)
            throws IOException {
        String[] parts = proxy.split(":", 4);
        if (parts.length != 4 || parts[0].length() == 0 || parts[1].length() == 0
                || parts[2].length() == 0 || parts[3].length() == 0) {
            throw new IOException("Invalid SOCKS5 proxy format");
        }

        int proxyPort;
        try {
            proxyPort = Integer.parseInt(parts[1]);
        } catch (NumberFormatException exception) {
            throw new IOException("Invalid SOCKS5 proxy port", exception);
        }
        byte[] username = parts[2].getBytes(StandardCharsets.UTF_8);
        byte[] password = parts[3].getBytes(StandardCharsets.UTF_8);
        byte[] target = targetHost.getBytes(StandardCharsets.UTF_8);
        if (proxyPort < 1 || proxyPort > 65535 || username.length > 255
                || password.length > 255 || target.length > 255) {
            throw new IOException("Invalid SOCKS5 proxy field length");
        }

        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(parts[0], proxyPort), 10000);
            socket.setSoTimeout(15000);
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            output.write(new byte[]{5, 2, 0, 2});
            output.flush();
            byte[] method = readFully(input, 2);
            if (method[0] != 5 || method[1] != 2) {
                throw new IOException("SOCKS5 username/password method rejected");
            }

            byte[] auth = new byte[3 + username.length + password.length];
            auth[0] = 1;
            auth[1] = (byte) username.length;
            System.arraycopy(username, 0, auth, 2, username.length);
            auth[2 + username.length] = (byte) password.length;
            System.arraycopy(password, 0, auth, 3 + username.length, password.length);
            output.write(auth);
            output.flush();
            byte[] authResponse = readFully(input, 2);
            if (authResponse[0] != 1 || authResponse[1] != 0) {
                throw new IOException("SOCKS5 authentication failed");
            }

            byte[] request = new byte[7 + target.length];
            request[0] = 5;
            request[1] = 1;
            request[2] = 0;
            request[3] = 3;
            request[4] = (byte) target.length;
            System.arraycopy(target, 0, request, 5, target.length);
            request[5 + target.length] = (byte) (targetPort >>> 8);
            request[6 + target.length] = (byte) targetPort;
            output.write(request);
            output.flush();

            byte[] response = readFully(input, 4);
            if (response[0] != 5 || response[1] != 0) {
                throw new IOException("SOCKS5 CONNECT failed: " + (response[1] & 255));
            }
            int addressType = response[3] & 255;
            if (addressType == 1) {
                readFully(input, 6);
            } else if (addressType == 3) {
                int length = readFully(input, 1)[0] & 255;
                readFully(input, length + 2);
            } else if (addressType == 4) {
                readFully(input, 18);
            } else {
                throw new IOException("Invalid SOCKS5 response address");
            }
            return socket;
        } catch (IOException exception) {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
            throw exception;
        }
    }

    private static byte[] readFully(InputStream input, int length) throws IOException {
        byte[] result = new byte[length];
        int offset = 0;
        while (offset < length) {
            int count = input.read(result, offset, length - offset);
            if (count < 0) {
                throw new IOException("Unexpected EOF from SOCKS5 proxy");
            }
            offset += count;
        }
        return result;
    }

    private static final class JavaSocketConnection implements SocketConnection {
        private final Socket socket;

        private JavaSocketConnection(Socket socket) {
            this.socket = socket;
        }

        public DataInputStream openDataInputStream() throws IOException {
            return new DataInputStream(socket.getInputStream());
        }

        public DataOutputStream openDataOutputStream() throws IOException {
            return new DataOutputStream(socket.getOutputStream());
        }

        public void close() throws IOException {
            socket.close();
        }
    }

    private static final class JavaHttpConnection implements HttpConnection {
        private final HttpURLConnection connection;

        private JavaHttpConnection(String url) throws IOException {
            this.connection = (HttpURLConnection) new URL(url).openConnection();
            this.connection.setConnectTimeout(15000);
            this.connection.setReadTimeout(15000);
        }

        public InputStream openInputStream() throws IOException {
            return connection.getInputStream();
        }

        public int getResponseCode() throws IOException {
            return connection.getResponseCode();
        }

        public void close() {
            connection.disconnect();
        }
    }
}
