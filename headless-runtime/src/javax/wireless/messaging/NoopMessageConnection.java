package javax.wireless.messaging;

import java.io.IOException;

public final class NoopMessageConnection implements MessageConnection {
    public Object newMessage(String type) {
        return new NoopTextMessage();
    }

    public void send(TextMessage message) throws IOException {
    }

    public void close() throws IOException {
    }

    private static final class NoopTextMessage implements TextMessage {
        public void setAddress(String address) {
        }

        public void setPayloadText(String text) {
        }
    }
}
