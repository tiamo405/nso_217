
public final class ChatPopup extends Effect2 implements IActionListener {

    private int sayWidth = 100;
    private int delay;
    private int sayRun;
    private String[] says;
    private int cx;
    private int cy;
    private int ch;
    private Char c;
    private boolean outSide = false;
    private int currentLine;
    private String[] lines;
    public Command cmdNextLine;
    public static ChatPopup currentMultilineChatPopup;

    public static void gameAA(String var0, Char var2) {
        String[] var3;
        if ((var3 = Res.split(var0, "\n", 0)).length == 1) {
            addChatPopup(var3[0], 1000, var2);
        } else {
            (currentMultilineChatPopup = addChatPopup(var3[0], 1000, var2)).currentLine = 0;
            currentMultilineChatPopup.lines = var3;
            currentMultilineChatPopup.cmdNextLine = new Command(mResources.gameCB, currentMultilineChatPopup, 8000, (Object) null);
        }
    }

    public static ChatPopup addChatPopup(String var0, int var1, Char var2) {
        ChatPopup var3 = new ChatPopup();
        if (var0.length() < 10) {
            var3.sayWidth = 64;
        }

        if (GameCanvas.w == 128) {
            var3.sayWidth = 128;
        }

        var3.says = mFont.tahoma_7_red.gameAB(var0, var3.sayWidth - 10);
        var3.delay = var1;
        var3.c = var2;
        var3.cx = var2.cx;
        var3.cy = var2.cy;
        var2.chatPopup = var3;
        var3.sayRun = 7;
        Effect2.vEffect2.addElement(var3);
        return var3;
    }

    public final void update() {
        if (this.c != null) {
            this.cx = this.c.cx;
            this.cy = this.c.cy;
            this.ch = this.c.ch + 10;
        }

        if (this.delay > 0) {
            --this.delay;
        }

        if (this.sayRun > 1) {
            --this.sayRun;
        }

        if (this.c != null && this.c.chatPopup != null && this.c.chatPopup != this || this.c != null && this.c.chatPopup == null || this.delay == 0) {
            Effect2.vEffect2Outside.removeElement(this);
            Effect2.vEffect2.removeElement(this);
        }

    }

    public final void paint(mGraphics var1) {
        int var2 = this.cx;
        int var3 = this.cy;
        var1.gameAA(16777215);
        var1.gameAB(var2 - this.sayWidth / 2 - 1, var3 - this.ch - 15 + this.sayRun - this.says.length * 12 - 10, this.sayWidth + 2, (this.says.length + 1) * 12 + 1, 12, 12);
        var1.gameAA(0);
        var1.gameAA(var2 - this.sayWidth / 2 - 1, var3 - this.ch - 15 + this.sayRun - this.says.length * 12 - 10, this.sayWidth + 1, (this.says.length + 1) * 12, 12, 12);
        SmallImage.gameAA(var1, 941, var2 - 3, var3 - this.ch - 15 + this.sayRun + 2, 0, 17);

        for (int var4 = 0; var4 < this.says.length; ++var4) {
            mFont.tahoma_7.gameAA(var1, this.says[var4], var2, var3 - this.ch - 15 + this.sayRun + var4 * 12 - this.says.length * 12 - 4, 2);
        }

    }

    public final void perform(int var1, Object var2) {
        if (var1 == 8000) {
            int var3 = currentMultilineChatPopup.currentLine;
            ++var3;
            if (var3 >= currentMultilineChatPopup.lines.length) {
                currentMultilineChatPopup.c.chatPopup = null;
                currentMultilineChatPopup = null;
                return;
            }

            ChatPopup var4;
            (var4 = addChatPopup(currentMultilineChatPopup.lines[var3], currentMultilineChatPopup.delay, currentMultilineChatPopup.c)).currentLine = var3;
            var4.lines = currentMultilineChatPopup.lines;
            var4.cmdNextLine = currentMultilineChatPopup.cmdNextLine;
            currentMultilineChatPopup = var4;
        }

    }
}
