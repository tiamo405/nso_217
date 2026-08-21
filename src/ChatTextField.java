public final class ChatTextField implements IActionListener {
   private static ChatTextField instance;
   public TField tfChat;
   public boolean isShow = false;
   IChatable gameAC;
   private long lastChatTime = 0L;
   public Command left;
   public Command right;
   public Command center;
   public String to;

   public static ChatTextField gameAA() {
      return instance == null ? (instance = new ChatTextField()) : instance;
   }

   protected ChatTextField() {
      this.left = new Command(mResources.gameCW, this, 8000, (Object)null, 1, GameCanvas.h - mScreen.cmdH + 1);
      this.right = new Command(mResources.gameBW, this, 8001, (Object)null, GameCanvas.w - 53, GameCanvas.h - mScreen.cmdH + 1);
      this.center = null;
      this.tfChat = new TField();
      this.tfChat.name = "chat";
      this.tfChat.x = 16;
      this.tfChat.width = MotherCanvas.instance.gameAB() - 32;
      this.tfChat.height = mScreen.ITEM_HEIGHT + 2;
      this.tfChat.isFocus = true;
      this.tfChat.setMaxTextLenght(40);
   }

   public final void gameAA(int var1, IChatable var2, String var3) {
      this.right.caption = mResources.gameBH;
      this.to = var3;
      this.tfChat.gameAA(var1);
      if (!this.tfChat.getText().equals("") && GameCanvas.currentDialog == null) {
         this.gameAC = var2;
         this.isShow = true;
      }

      this.tfChat.title_Null = var3;
   }

   public final void setText(String var1) {
      this.right.caption = mResources.gameBH;
      this.to = var1;
      if (GameCanvas.currentDialog == null) {
         this.isShow = true;
         if (GameCanvas.isTouch) {
            this.tfChat.update();
         }
      }

      this.tfChat.title_Null = var1;
   }

   public final void paint(mGraphics var1) {
      if (this.isShow) {
         this.tfChat.paint(var1);
      }
   }

   public final void perform(int var1, Object var2) {
      switch(var1) {
      case 8000:
         if (this.gameAC != null) {
            long var3;
            if ((var3 = System.currentTimeMillis()) - this.lastChatTime < 1000L) {
               return;
            }

            this.lastChatTime = var3;
            this.gameAC.gameAA(this.tfChat.getText(), this.to);
            this.tfChat.setText("");
            this.right.caption = mResources.gameBH;
            return;
         }
         break;
      case 8001:
         this.tfChat.gameAB();
         if (this.tfChat.getText().equals("")) {
            this.isShow = false;
            this.gameAC.gameBK();
         }
      }

   }
}
