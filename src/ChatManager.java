public final class ChatManager {
   public MyVector chatTabs = new MyVector();
   private static ChatManager instance;
   public int currentTabIndex = 0;
   public static boolean blockGlobalChat;
   public static boolean blockPrivateChat;
   public static boolean isMessageClan;
   public static boolean isMessagePt;
   public MyVector waitList = new MyVector();

   public final void gameAA() {
      ++this.currentTabIndex;
      if (this.currentTabIndex > this.chatTabs.size() - 1) {
         this.currentTabIndex = 0;
      }

   }

   public final void gameAB() {
      --this.currentTabIndex;
      if (this.currentTabIndex < 0) {
         this.currentTabIndex = this.chatTabs.size() - 1;
      }

   }

   public final void gameAA(int var1) {
      this.currentTabIndex = var1;
   }

   public final void gameAA(ChatTab var1) {
      this.currentTabIndex = this.chatTabs.indexOf(var1);
   }

   public final void gameAC() {
      this.currentTabIndex = this.chatTabs.size() - 1;
   }

   public static ChatManager gameAD() {
      return instance == null ? (instance = new ChatManager()) : instance;
   }

   public ChatManager() {
      this.chatTabs.addElement(new ChatTab(mResources.gameTI[0], 0));
      this.chatTabs.addElement(new ChatTab(mResources.gameTJ[0], 1));
      this.chatTabs.addElement(new ChatTab(mResources.gameTK[0], 3));
      this.chatTabs.addElement(new ChatTab(mResources.gameTL[0], 4));
      ChatTab var1;
      (var1 = this.gameAA(mResources.gameTK[0])).gameAA("c8" + mResources.gameTK[1]);
      var1.gameAA("c8" + mResources.gameTK[2]);
      var1.gameAA("c8" + mResources.gameTK[3]);
      this.gameAA(mResources.gameTJ[0]).gameAA("c8" + mResources.gameTJ[1]);
      this.gameAA(mResources.gameTL[0]).gameAA("c8" + mResources.gameTL[1]);
      this.gameAA(mResources.gameTI[0]).gameAA("c8" + mResources.gameTI[1]);
   }

   public final ChatTab gameAA(String var1) {
      for(int var2 = 0; var2 < this.chatTabs.size(); ++var2) {
         ChatTab var3;
         if ((var3 = (ChatTab)this.chatTabs.elementAt(var2)).ownerName.equals(var1)) {
            return var3;
         }
      }

      return null;
   }

   public final void gameAA(String var1, String var2, String var3) {
      ChatTab var4;
      if ((var4 = this.gameAA(var1)) == null) {
         var4 = this.gameAB(var1);
      }

      var4.gameAA(var2, var3);
   }

   public final ChatTab gameAE() {
      return (ChatTab)this.chatTabs.elementAt(this.currentTabIndex);
   }

   public final ChatTab gameAB(String var1) {
      ChatTab var2 = new ChatTab(var1, 2);
      if (!GameCanvas.isTouch) {
         var2.gameAA("c2" + mResources.gameTH);
      }

      this.chatTabs.addElement(var2);
      return var2;
   }

   public final void gameAC(String var1) {
      for(int var2 = 0; var2 < this.waitList.size(); ++var2) {
         if (((String)this.waitList.elementAt(var2)).equals(var1)) {
            return;
         }
      }

      this.waitList.addElement(var1);
   }

   public final boolean gameAD(String var1) {
      for(int var2 = 0; var2 < this.waitList.size(); ++var2) {
         if (((String)this.waitList.elementAt(var2)).equals(var1)) {
            return true;
         }
      }

      return false;
   }

   public final void gameAE(String var1) {
      for(int var2 = 0; var2 < this.waitList.size(); ++var2) {
         if (((String)this.waitList.elementAt(var2)).equals(var1)) {
            this.waitList.removeElementAt(var2);
            return;
         }
      }

   }

   public static void gameAF() {
      instance = null;
   }
}
