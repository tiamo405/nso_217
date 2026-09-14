package javax.microedition.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface SocketConnection {
    DataInputStream openDataInputStream() throws IOException;

    DataOutputStream openDataOutputStream() throws IOException;

    void close() throws IOException;
}
