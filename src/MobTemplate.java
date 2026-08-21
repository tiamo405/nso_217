import javax.microedition.lcdui.Image;

public final class MobTemplate {
   public byte rangeMove;
   public byte speed;
   public byte type;
   public byte typeFly = 0;
   public short mobTemplateId;
   public int hp;
   public String name;
   public Image[] imgs;
   public ImageInfo[] imginfo;
   public Frame[] frameBoss;
   public byte[] frameBossMove;
   public byte[][] frameBossAttack;
   public byte[][] frameChar = new byte[4][];
   public byte[] sequence;
   public byte[] indexSplash = new byte[4];
}
