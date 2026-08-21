package javax.wireless.messaging;

import java.io.IOException;

public interface MessageConnection {
    Object newMessage(String type);

    void send(TextMessage message) throws IOException;

    void close() throws IOException;
}
