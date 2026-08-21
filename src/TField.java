import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.TextBox;
import javax.microedition.midlet.MIDlet;

public final class TField implements IActionListener {
   public int x;
   public int y;
   public int width;
   public int height;
   public boolean isFocus;
   private boolean gameAM = false;
   private static int typeXpeed = 2;
   private static final int[] MAX_TIME_TO_CONFIRM_KEY = new int[]{18, 14, 11, 9, 6, 4, 2};
   private static int CARET_HEIGHT = 0;
   private static String[] print = new String[]{" 0", ".,@?!_1\"/$-():*+<=>;%&~#%^&*{}[];'/1", "abc2áàảãạâấầẩẫậăắằẳẵặ2", "def3đéèẻẽẹêếềểễệ3", "ghi4íìỉĩị4", "jkl5", "mno6óòỏõọôốồổỗộơớờởỡợ6", "pqrs7", "tuv8úùủũụưứừửữự8", "wxyz9ýỳỷỹỵ9", "*", "#"};
   private static String[] printA = new String[]{"0", "1", "abc2", "def3", "ghi4", "jkl5", "mno6", "pqrs7", "tuv8", "wxyz9", "0", "0"};
   public String gameAF = "";
   public String title_Null = "";
   private String text = "";
   private String passwordText = "";
   private String paintedText = "";
   private int caretPos = 0;
   private int counter = 0;
   private int maxTextLenght = 500;
   private int offsetX = 0;
   private int lastKey = -1984;
   private int keyInActiveState = 0;
   private int indexOfActiveChar = 0;
   private int showCaretCounter = 10;
   private int inputType = 0;
   public static boolean isQwerty;
   private static int gameBE;
   private int gameBF = 0;
   private static int changeModeKey;
   public static Canvas gameAI;
   public static MIDlet gameAJ;
   public String name = "";
   public Command cmdClear;
   public boolean fieldAI;

   static {
      String[] var10000 = new String[]{"abc", "Abc", "ABC", "123"};
      changeModeKey = 11;
      int[][] var0 = new int[][]{{32, 48}, {49, 69}, {50, 84}, {51, 85}, {52, 68}, {53, 71}, {54, 74}, {55, 67}, {56, 66}, {57, 77}, {42, 128}, {35, 137}, {33, 113}, {63, 97}, {64, 121, 122}, {46, 111}, {44, 108}};
   }

   public final void update() {
      TextBox var1;
      (var1 = new TextBox(this.name, "", this.maxTextLenght, 0)).addCommand(new javax.microedition.lcdui.Command(mResources.gameCX, 4, 0));
      var1.addCommand(new javax.microedition.lcdui.Command("Cancel", 3, 0));
      var1.setCommandListener(new Command_Listener(this, var1));

      try {
         if (this.inputType == 2) {
            var1.setConstraints(65536);
         } else if (this.inputType == 1) {
            var1.setConstraints(2);
         } else {
            var1.setConstraints(0);
         }
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      var1.setString(this.text);
      var1.setMaxSize(this.maxTextLenght);
      Display.getDisplay(gameAJ).setCurrent(var1);
   }

   public static boolean gameAA(char var0) {
      return var0 >= '0' && var0 <= '9' || var0 >= 'A' && var0 <= 'Z' || var0 >= 'a' && var0 <= 'z';
   }

   public TField() {
      this.text = "";
      CARET_HEIGHT = mFont.tahoma_8b.gameAC() + 1;
      this.cmdClear = new Command(mResources.gameBW, this, 1000, (Object)null);
      gameBE = 0;
      if (RMS.gameAC("qwerty") == 1) {
         isQwerty = true;
      }

   }

   public final void gameAB() {
      if (this.caretPos > 0 && this.text.length() > 0) {
         this.text = this.text.substring(0, this.caretPos - 1) + this.text.substring(this.caretPos, this.text.length());
         --this.caretPos;
         this.setOffset();
         this.setPasswordTest();
      }

   }

   private void setOffset() {
      if (this.inputType == 2) {
         this.paintedText = this.passwordText;
      } else {
         this.paintedText = this.text;
      }

      if (this.offsetX < 0 && mFont.tahoma_8b.gameAA(this.paintedText) + this.offsetX < this.width - 4 - 13) {
         this.offsetX = this.width - 10 - mFont.tahoma_8b.gameAA(this.paintedText);
      }

      if (this.offsetX + mFont.tahoma_8b.gameAA(this.paintedText.substring(0, this.caretPos)) <= 0) {
         this.offsetX = -mFont.tahoma_8b.gameAA(this.paintedText.substring(0, this.caretPos));
         this.offsetX += 40;
      } else if (this.offsetX + mFont.tahoma_8b.gameAA(this.paintedText.substring(0, this.caretPos)) >= this.width - 12) {
         this.offsetX = this.width - 10 - mFont.tahoma_8b.gameAA(this.paintedText.substring(0, this.caretPos)) - 8;
      }

      if (this.offsetX > 0) {
         this.offsetX = 0;
      }

   }

   private void gameAD(int var1) {
      if (this.inputType != 2 && this.inputType != 3 || var1 >= 48 && var1 <= 57 || var1 >= 65 && var1 <= 90 || var1 >= 97 && var1 <= 122) {
         if (this.text.length() < this.maxTextLenght) {
            String var2 = this.text.substring(0, this.caretPos) + (char)var1;
            if (this.caretPos < this.text.length()) {
               var2 = var2 + this.text.substring(this.caretPos, this.text.length());
            }

            this.text = var2;
            ++this.caretPos;
            this.setPasswordTest();
            this.setOffset();
         }

      }
   }

   public final boolean gameAA(int var1) {
      if (var1 != 8 && var1 != -8 && var1 != 204) {
         if (var1 >= 65 && var1 <= 122 && !isQwerty) {
            isQwerty = true;
            gameBE = 0;
            RMS.gameAA("qwerty", 1);
         }

         if (isQwerty) {
            if (var1 == 45) {
               if (var1 == this.lastKey && this.keyInActiveState < MAX_TIME_TO_CONFIRM_KEY[typeXpeed]) {
                  this.text = this.text.substring(0, this.caretPos - 1) + '_';
                  this.paintedText = this.text;
                  this.setPasswordTest();
                  this.setOffset();
                  this.lastKey = -1984;
                  return false;
               }

               this.lastKey = 45;
            }

            if (var1 >= 32) {
               this.gameAD(var1);
               return false;
            }
         }

         if (var1 == changeModeKey) {
            ++this.gameBF;
            if (this.gameBF > 3) {
               this.gameBF = 0;
            }

            this.keyInActiveState = 1;
            this.lastKey = var1;
            return false;
         } else {
            if (var1 == 42) {
               var1 = 58;
            }

            if (var1 == 35) {
               var1 = 59;
            }

            if (var1 >= 48 && var1 <= 59) {
               if (this.inputType != 0 && this.inputType != 2 && this.inputType != 3) {
                  if (this.inputType == 1) {
                     this.gameAD(var1);
                     this.keyInActiveState = 1;
                  }
               } else {
                  String[] var2;
                  TField var4;
                  if ((var4 = this).inputType != 2 && var4.inputType != 3) {
                     var2 = print;
                  } else {
                     var2 = printA;
                  }

                  char var3;
                  String var5;
                  if (var1 == var4.lastKey) {
                     var4.indexOfActiveChar = (var4.indexOfActiveChar + 1) % var2[var1 - 48].length();
                     var3 = var2[var1 - 48].charAt(var4.indexOfActiveChar);
                     if (var4.gameBF == 0) {
                        var3 = Character.toLowerCase(var3);
                     } else if (var4.gameBF == 1) {
                        var3 = Character.toUpperCase(var3);
                     } else if (var4.gameBF == 2) {
                        var3 = Character.toUpperCase(var3);
                     } else {
                        var3 = var2[var1 - 48].charAt(var2[var1 - 48].length() - 1);
                     }

                     var5 = var4.text.substring(0, var4.caretPos - 1) + var3;
                     if (var4.caretPos < var4.text.length()) {
                        var5 = var5 + var4.text.substring(var4.caretPos, var4.text.length());
                     }

                     var4.text = var5;
                     var4.keyInActiveState = MAX_TIME_TO_CONFIRM_KEY[typeXpeed];
                     var4.setPasswordTest();
                  } else if (var4.text.length() < var4.maxTextLenght) {
                     if (var4.gameBF == 1 && var4.lastKey != -1984) {
                        var4.gameBF = 0;
                     }

                     var4.indexOfActiveChar = 0;
                     var3 = var2[var1 - 48].charAt(var4.indexOfActiveChar);
                     if (var4.gameBF == 0) {
                        var3 = Character.toLowerCase(var3);
                     } else if (var4.gameBF == 1) {
                        var3 = Character.toUpperCase(var3);
                     } else if (var4.gameBF == 2) {
                        var3 = Character.toUpperCase(var3);
                     } else {
                        var3 = var2[var1 - 48].charAt(var2[var1 - 48].length() - 1);
                     }

                     var5 = var4.text.substring(0, var4.caretPos) + var3;
                     if (var4.caretPos < var4.text.length()) {
                        var5 = var5 + var4.text.substring(var4.caretPos, var4.text.length());
                     }

                     var4.text = var5;
                     var4.keyInActiveState = MAX_TIME_TO_CONFIRM_KEY[typeXpeed];
                     ++var4.caretPos;
                     var4.setPasswordTest();
                     var4.setOffset();
                  }

                  var4.lastKey = var1;
               }
            } else {
               this.indexOfActiveChar = 0;
               this.lastKey = -1984;
               if (var1 == 14) {
                  if (this.caretPos > 0) {
                     --this.caretPos;
                     this.setOffset();
                     this.showCaretCounter = 10;
                     return false;
                  }
               } else if (var1 == 15) {
                  if (this.caretPos < this.text.length()) {
                     ++this.caretPos;
                     this.setOffset();
                     this.showCaretCounter = 10;
                     return false;
                  }
               } else {
                  if (var1 == 19) {
                     this.gameAB();
                     return false;
                  }

                  this.lastKey = var1;
               }
            }

            return true;
         }
      } else {
         this.gameAB();
         return true;
      }
   }

   public final void paint(mGraphics var1) {
      var1.gameAD(0, 0, GameCanvas.w, GameCanvas.h);
      boolean var2 = this.isFocus;
      if (this.inputType == 2) {
         this.paintedText = this.passwordText;
      } else {
         this.paintedText = this.text;
      }

      if (this.paintedText.equals("")) {
         this.paintedText = this.title_Null;
      }

      Paint.gameAA(var1, var2, this.x, this.y, this.width, this.height, 4 + this.offsetX + this.x, this.y + (this.height - mFont.tahoma_8b.gameAC()) / 2, this.paintedText);
      var1.gameAD(this.x + 3, this.y + 1, this.width - 4, this.height - 4);
      var1.gameAA(0);
      if (this.isFocus && this.keyInActiveState == 0 && (this.showCaretCounter > 0 || this.counter / 5 % 2 == 0)) {
         var1.gameAA(11184810);
         var1.gameAC(5 + this.offsetX + this.x + mFont.tahoma_8b.gameAA(this.paintedText.substring(0, this.caretPos)) - 1, this.y + (this.height - CARET_HEIGHT) / 2 + 1, 1, CARET_HEIGHT);
      }

   }

   public final void gameAB(mGraphics var1) {
      boolean var2 = this.isFocus;
      if (this.inputType == 2) {
         this.paintedText = this.passwordText;
      } else {
         this.paintedText = this.text;
      }

      if (this.paintedText.equals("")) {
         this.paintedText = this.title_Null;
      }

      Paint.gameAA(var1, var2, this.x, this.y, this.width, 4 + this.offsetX + this.x, this.y + (this.height - mFont.tahoma_8b.gameAC()) / 2, this.paintedText);
      var1.gameAA(0);
      if (this.isFocus && this.keyInActiveState == 0 && (this.showCaretCounter > 0 || this.counter / 5 % 2 == 0)) {
         var1.gameAA(11184810);
         var1.gameAC(5 + this.offsetX + this.x + mFont.tahoma_8b.gameAA(this.paintedText.substring(0, this.caretPos)) - 1, this.y + (this.height - CARET_HEIGHT) / 2 + 1, 1, CARET_HEIGHT);
      }

   }

   private void setPasswordTest() {
      if (this.inputType == 2) {
         this.passwordText = "";

         for(int var1 = 0; var1 < this.text.length(); ++var1) {
            this.passwordText = this.passwordText + "*";
         }

         if (this.keyInActiveState > 0 && this.caretPos > 0) {
            this.passwordText = this.passwordText.substring(0, this.caretPos - 1) + this.text.charAt(this.caretPos - 1) + this.passwordText.substring(this.caretPos, this.passwordText.length());
         }
      }

   }

   public final void gameAC() {
      ++this.counter;
      if (this.keyInActiveState > 0) {
         --this.keyInActiveState;
         if (this.keyInActiveState == 0) {
            this.indexOfActiveChar = 0;
            if (this.gameBF == 1 && this.lastKey != changeModeKey) {
               this.gameBF = 0;
            }

            this.lastKey = -1984;
            this.setPasswordTest();
         }
      }

      if (this.showCaretCounter > 0) {
         --this.showCaretCounter;
      }

      if (GameCanvas.isPointerJustRelease) {
         if (GameCanvas.currentScreen == RegisterScr.instance) {
            if (RegisterScr.instance == null) {
               RegisterScr.instance = new RegisterScr();
            }

            int var1 = RegisterScr.instance.scroll.cmy;
            if (!GameCanvas.gameAB(this.x, this.y - var1, this.width, this.height)) {
               this.isFocus = false;
               return;
            }

            this.update();
         } else {
            if (GameCanvas.gameAB(this.x, this.y, this.width, this.height)) {
               this.update();
               return;
            }

            this.isFocus = false;
         }
      }

   }

   public final String getText() {
      return this.text;
   }

   public final void setText(String var1) {
      if (var1 != null) {
         this.lastKey = -1984;
         this.keyInActiveState = 0;
         this.indexOfActiveChar = 0;
         this.text = var1;
         this.paintedText = var1;
         this.setPasswordTest();
         this.caretPos = var1.length();
         this.setOffset();
      }
   }

   public final void setMaxTextLenght(int var1) {
      this.maxTextLenght = var1;
   }

   public final void setIputType(int var1) {
      this.inputType = var1;
   }

   public final void perform(int var1, Object var2) {
      switch(var1) {
      case 1000:
         this.gameAB();
      default:
      }
   }
}
