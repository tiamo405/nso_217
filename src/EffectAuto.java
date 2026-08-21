import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Enumeration;
import java.util.Hashtable;

public final class EffectAuto extends Effect2 {
   private short id;
   private MyImage img;
   private int x;
   private int y;
   private int f;
   private int dir;
   private byte loopCount = 0;
   private long endTime = 0L;
   private static EffAtutoTemp[] arrEffAtutoTemplate = new EffAtutoTemp[20];
   private static Hashtable imgEffAuto = new Hashtable();
   private static Hashtable dataEffAuto = new Hashtable();
   private boolean isInfinite_loop;

   public static void gameAA(short var0, int var1, int var2, byte var3, short var4, int var5) {
      EffectAuto var6;
      (var6 = new EffectAuto()).id = var0;
      var6.x = var1;
      var6.y = var2;
      var6.loopCount = var3;
      if (var6.loopCount < 0) {
         var6.isInfinite_loop = true;
      } else {
         var6.isInfinite_loop = false;
      }

      if (var4 > 0) {
         var6.endTime = System.currentTimeMillis() + (long)(var4 * 1000);
      } else {
         var6.endTime = -1L;
      }

      var6.dir = var5;
      Effect2.vEffect2.addElement(var6);
   }

   private EffAtutoTemp gameAD() {
      return arrEffAtutoTemplate[this.id];
   }

   public final void paint(mGraphics var1) {
      if (this.img != null && this.img.img != null && this.gameAD().frameRunning != null) {
         Frame var2 = this.gameAD().frameEffAuto[this.gameAD().frameRunning[this.f]];

         for(int var3 = 0; var3 < var2.dx.length; ++var3) {
            EffAtutoTemp var10000 = this.gameAD();
            byte var5 = var2.idImg[var3];
            ImageInfo var4 = var10000.imginfo[var5];
            if (this.dir > 0) {
               var1.gameAA(this.img.img, var4.x0, var4.y0, var4.w, var4.h, 0, this.x + var2.dx[var3], this.y + var2.dy[var3] - 1, 20);
            } else {
               var1.gameAA(this.img.img, var4.x0, var4.y0, var4.w, var4.h, 2, this.x - var2.dx[var3], this.y + var2.dy[var3] - 1, 24);
            }
         }
      }

   }

   public final void update() {
      try {
         arrEffAtutoTemplate[this.id] = (EffAtutoTemp) dataEffAuto.get(String.valueOf(this.id));
         if (arrEffAtutoTemplate[this.id] == null) {
            arrEffAtutoTemplate[this.id] = new EffAtutoTemp();
            dataEffAuto.put(String.valueOf(this.id), arrEffAtutoTemplate[this.id]);
            this.gameAD().timerequest = System.currentTimeMillis();
            Service.gI().request_imgEffAuto((byte)1, this.id);
         } else if (this.gameAD().frameRunning == null && System.currentTimeMillis() - this.gameAD().timerequest > 3000L) {
            this.gameAD().timerequest = System.currentTimeMillis();
            Service.gI().request_imgEffAuto((byte)1, this.id);
         }

         if (this.gameAD().frameRunning != null) {
            this.img = (MyImage) imgEffAuto.get(String.valueOf(this.id));
            if (this.img == null) {
               this.img = new MyImage();
               imgEffAuto.put(String.valueOf(this.id), this.img);
               this.img.img = Controller.gameAA(RMS.gameAA("effauto " + this.id));
               if (this.img.img == null) {
                  this.img.timerequest = System.currentTimeMillis();
                  Service.gI().request_imgEffAuto((byte)0, this.id);
               }
            } else if (this.img.img == null && System.currentTimeMillis() - this.img.timerequest > 6000L) {
               this.img.timerequest = System.currentTimeMillis();
               Service.gI().request_imgEffAuto((byte)0, this.id);
            }
         }

         if (this.img != null && this.img.img != null && this.gameAD().frameRunning != null) {
            ++this.f;
            if (this.f >= this.gameAD().frameRunning.length) {
               if (this.endTime != -1L) {
                  if (this.endTime - System.currentTimeMillis() <= 0L) {
                     Effect2.vEffect2.removeElement(this);
                  } else {
                     this.f = 0;
                  }
               } else {
                  label62: {
                     if (!this.isInfinite_loop) {
                        --this.loopCount;
                        if (this.loopCount <= 0) {
                           Effect2.vEffect2.removeElement(this);
                           break label62;
                        }
                     }

                     this.f = 0;
                  }
               }
            }

            this.img.timeUse = System.currentTimeMillis();
            return;
         }
      } catch (Exception var1) {
         System.out.println("Err update effauto:" + var1.toString());
      }

   }

   public static void gameAA(short var0, byte[] var1) {
      if (((EffAtutoTemp) dataEffAuto.get(String.valueOf(var0))).frameRunning == null) {
         new EffAtutoTemp();
         EffAtutoTemp var2 = gameAC(var0, var1);
         dataEffAuto.put(String.valueOf(var0), var2);
      }

   }

   private static EffAtutoTemp gameAC(short var0, byte[] var1) {
      try {
         EffAtutoTemp var6 = new EffAtutoTemp();
         ByteArrayInputStream var7 = new ByteArrayInputStream(var1);
         DataInputStream var8 = new DataInputStream(var7);
         var6.imginfo = new ImageInfo[var8.readByte()];

         int var2;
         for(var2 = 0; var2 < var6.imginfo.length; ++var2) {
            var6.imginfo[var2] = new ImageInfo();
            var8.readByte();
            var6.imginfo[var2].x0 = var8.readUnsignedByte();
            var6.imginfo[var2].y0 = var8.readUnsignedByte();
            var6.imginfo[var2].w = var8.readUnsignedByte();
            var6.imginfo[var2].h = var8.readUnsignedByte();
         }

         var6.frameEffAuto = new Frame[var8.readShort()];

         for(var2 = 0; var2 < var6.frameEffAuto.length; ++var2) {
            var6.frameEffAuto[var2] = new Frame();
            byte var3 = var8.readByte();
            var6.frameEffAuto[var2].dx = new short[var3];
            var6.frameEffAuto[var2].dy = new short[var3];
            var6.frameEffAuto[var2].idImg = new byte[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               var6.frameEffAuto[var2].dx[var4] = var8.readShort();
               var6.frameEffAuto[var2].dy[var4] = var8.readShort();
               var6.frameEffAuto[var2].idImg[var4] = var8.readByte();
            }
         }

         short var9 = var8.readShort();
         var6.frameRunning = new short[var9];

         for(int var10 = 0; var10 < var9; ++var10) {
            var6.frameRunning[var10] = var8.readShort();
         }

         return var6;
      } catch (Exception var5) {
         return null;
      }
   }

   public static void gameAB(short var0, byte[] var1) {
      MyImage var2;
      if ((var2 = (MyImage) imgEffAuto.get(String.valueOf(var0))) == null) {
         var2 = new MyImage();
         imgEffAuto.put(String.valueOf(var0), var2);
      }

      var2.img = Controller.gameAA(var1);
      if (GameMidlet.CLIENT_TYPE != 1) {
         RMS.gameAB("effauto " + var0, var1);
      }

   }

   public static void gameAB() {
      try {
         Enumeration var0 = imgEffAuto.keys();

         while(var0.hasMoreElements()) {
            String var1 = (String)var0.nextElement();
            MyImage var2 = (MyImage) imgEffAuto.get(var1);
            if (System.currentTimeMillis() - var2.timeUse > 60000L) {
               imgEffAuto.remove(var1);
            }
         }

      } catch (Exception var3) {
      }
   }

   public static void gameAC() {
      try {
         Enumeration var0 = dataEffAuto.keys();

         while(var0.hasMoreElements()) {
            String var1 = (String)var0.nextElement();
            EffAtutoTemp var2 = (EffAtutoTemp) dataEffAuto.get(var1);
            if (System.currentTimeMillis() - var2.timeUse > 600000L) {
               dataEffAuto.remove(var1);
            }
         }

      } catch (Exception var3) {
      }
   }
}
