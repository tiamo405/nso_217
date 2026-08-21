public abstract class Effect2 {
   public static MyVector vEffect2 = new MyVector();
   public static MyVector vRemoveEffect2 = new MyVector();
   public static MyVector vEffect2Outside = new MyVector();
   public static MyVector vAnimateEffect = new MyVector();

   public abstract void update();

   public abstract void paint(mGraphics var1);
}
