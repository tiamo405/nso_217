import java.util.Hashtable;

public final class mHashtable1 extends Hashtable {
   public final synchronized Object gameAA(String var1, Object var2) {
      return super.put(var1, var2);
   }

   public final synchronized void clear() {
      super.clear();
   }

   public final synchronized Object remove(Object var1) {
      return super.remove(var1);
   }

   public final synchronized Object gameAA(String var1) {
      return super.get(var1);
   }
}
