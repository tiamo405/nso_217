package javax.microedition.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

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
        return new JavaSocketConnection(new Socket(host, port));
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
