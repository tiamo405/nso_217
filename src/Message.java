
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class Message {

    public byte command;
    private ByteArrayOutputStream bos = null;
    private DataOutputStream dos = null;
    private ByteArrayInputStream bis = null;
    private DataInputStream dis = null;

    public Message() {
    }

    public Message(byte var1) {
        this.command = var1;
        this.bos = new ByteArrayOutputStream();
        this.dos = new DataOutputStream(this.bos);
    }

    public Message(byte var1, byte[] var2) {
        this.command = var1;
        this.bis = new ByteArrayInputStream(var2);
        this.dis = new DataInputStream(this.bis);
        Code.fieldAA(var1, var2);

    }

    public final byte[] getData() {
        return this.bos.toByteArray();
    }

    public final DataInputStream reader() {
        return this.dis;
    }

    public final DataOutputStream writer() {
        return this.dos;
    }

    public final void cleanup() {
        try {
            if (this.dis != null) {
                this.dis.close();
            }

            if (this.dos != null) {
                this.dos.close();
                return;
            }
        } catch (IOException var1) {
        }

    }
}
