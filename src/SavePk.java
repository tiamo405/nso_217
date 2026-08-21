import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public final class SavePk {
   private static MyVector fieldAA;

   static {
      (fieldAA = new MyVector()).removeAllElements();

      try {
         ByteArrayInputStream var0 = new ByteArrayInputStream(RMS.gameAA("V6PKS"));
         DataInputStream var1;
         int var2 = (var1 = new DataInputStream(var0)).readInt();

         for(int var3 = 0; var3 < var2; ++var3) {
            fieldAA.addElement(var1.readUTF());
         }

         var1.close();
         var0.close();
      } catch (Exception var4) {
         var4.printStackTrace();
      }
   }

   private static void fieldAC() {
      ByteArrayOutputStream var0 = new ByteArrayOutputStream();
      DataOutputStream var1 = new DataOutputStream(var0);

      try {
         var1.writeInt(fieldAA.size());

         for(int var2 = 0; var2 < fieldAA.size(); ++var2) {
            var1.writeUTF((String)fieldAA.elementAt(var2));
         }

         var1.flush();
         var0.flush();
         RMS.gameAA("V6PKS", var0.toByteArray());
      } catch (Exception var3) {
         var3.printStackTrace();
      }
   }

   public static MyVector fieldAA() {
      MyVector var0 = new MyVector();

      for(int var1 = 0; var1 < fieldAA.size(); ++var1) {
         var0.addElement(var1 + ". " + (String)fieldAA.elementAt(var1));
      }

      return var0;
   }

   public static void fieldAA(String var0) {
      if (!fieldAA.contains(var0)) {
         fieldAA.addElement(var0);
         fieldAC();
      }

   }

   public static void fieldAB(String var0) {
      if (fieldAA.contains(var0)) {
         fieldAA.removeElement(var0);
         fieldAC();
      }

   }

   public static void fieldAB() {
      fieldAA.removeAllElements();
      fieldAC();
   }

   public static boolean fieldAC(String var0) {
      return fieldAA.contains(var0);
   }
}
