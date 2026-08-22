
import java.io.IOException;

final class MessageCollector implements Runnable {

    private Session_ME instance;

    MessageCollector(Session_ME var1) {
        this.instance = var1;
    }

    public final void run() {
        while (true) {
            try {
                if (this.instance.gameAB()) {
                    MessageCollector var10 = this;
                    byte var2 = this.instance.dis.readByte();
                    if (this.instance.getKeyComplete) {
                        var2 = Session_ME.gameAA(this.instance, var2);
                    }

                    int var3;
                    byte var4;
                    byte var5;
                    if (var2 == -32) {
                        var2 = this.instance.dis.readByte();
                        if (this.instance.getKeyComplete) {
                            var2 = Session_ME.gameAA(this.instance, var2);
                        }

                        var4 = Session_ME.gameAA(this.instance, this.instance.dis.readByte());
                        var5 = Session_ME.gameAA(this.instance, this.instance.dis.readByte());
                        byte var6 = Session_ME.gameAA(this.instance, this.instance.dis.readByte());
                        byte var7 = Session_ME.gameAA(this.instance, this.instance.dis.readByte());
                        var3 = (var4 & 255) << 24 | (var5 & 255) << 16 | (var6 & 255) << 8 | var7 & 255;
                    } else if (this.instance.getKeyComplete) {
                        var4 = this.instance.dis.readByte();
                        var5 = this.instance.dis.readByte();
                        var3 = (Session_ME.gameAA(this.instance, var4) & 255) << 8 | Session_ME.gameAA(this.instance, var5) & 255;
                    } else {
                        var3 = this.instance.dis.readUnsignedShort();
                    }

                    byte[] var12 = new byte[var3];
                    int var13 = 0;
                    int var14 = 0;

                    int var15;
                    while (var13 != -1 && var14 < var3) {
                        if ((var13 = var10.instance.dis.read(var12, var14, var3 - var14)) > 0) {
                            var14 += var13;
                            Session_ME var10000 = var10.instance;
                            var10000.recvByteCount += var14 + 5;
                            var15 = Session_ME.gI().recvByteCount + Session_ME.gI().sendByteCount;
                            var10.instance.gameAO = var15 / 1024 + "." + var15 % 1024 / 102 + "Kb";
                        }
                    }

                    if (var10.instance.getKeyComplete) {
                        for (var15 = 0; var15 < var12.length; ++var15) {
                            var12[var15] = Session_ME.gameAA(var10.instance, var12[var15]);
                        }
                    }

                    Message var11 = new Message(var2, var12);

                    try {
                        if (var11.command == -27) {
                            this.gameAA(var11);
                            continue;
                        }

                        this.instance.messageHandler.gameAA(var11);
                    } catch (Exception var8) {
                        var8.printStackTrace();
                    }
                    continue;
                }
            } catch (Exception var9) {
                if (this.isNormalSocketClose(var9)) {
                    System.out.println("AUTO LOGIN SOCKET CLOSED: " + var9.toString());
                } else {
                    System.out.println("AUTO LOGIN SOCKET ERROR: " + var9.toString());
                    var9.printStackTrace();
                }
            }

            boolean var16 = false;
            synchronized (this.instance) {
                if (this.instance.connected) {
                    this.instance.connected = false;
                    this.instance.connecting = false;
                    var16 = true;
                }
            }

            if (var16) {
                if (this.instance.messageHandler != null) {
                    if (System.currentTimeMillis() - this.instance.gameAN > 500L) {
                        Controller var1 = this.instance.messageHandler;
                        //GameCanvas.instance.gameAN();
                        var1.fieldAD();

                    } else {
                        this.instance.messageHandler.gameAB();
                    }
                }

                if (this.instance.fieldAE != null) {
                    Session_ME.gameAC(this.instance);
                }
            }

            return;
        }
    }

    private boolean isNormalSocketClose(Exception var1) {
        String var2 = var1.toString();
        return var1 instanceof IOException
                || var2.indexOf("Socket closed") >= 0
                || var2.indexOf("EOFException") >= 0
                || var2.indexOf("Encryption key is not ready") >= 0;
    }

    private void gameAA(Message var1) {
        try {
            byte var2 = var1.reader().readByte();
            this.instance.key = new byte[var2];

            int var3;
            for (var3 = 0; var3 < var2; ++var3) {
                this.instance.key[var3] = var1.reader().readByte();
            }

            for (var3 = 0; var3 < this.instance.key.length - 1; ++var3) {
                byte[] var10000 = this.instance.key;
                var10000[var3 + 1] ^= this.instance.key[var3];
            }

            this.instance.getKeyComplete = true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
