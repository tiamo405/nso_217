package javax.microedition.io;

import java.io.IOException;
import java.io.InputStream;

public interface HttpConnection {
    InputStream openInputStream() throws IOException;

    int getResponseCode() throws IOException;

    void close() throws IOException;
}
