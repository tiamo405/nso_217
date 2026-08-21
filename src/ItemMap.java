
public final class ItemMap {

    public int x;
    public int y;
    public int xEnd;
    public int yEnd;
    public int vx;
    public int vy;
    public int itemMapID;
    public ItemTemplate template;
    public byte status;
    public MyImage imgCaptcha;
    public boolean fieldAK = false;
    public long fieldAL = 0L;

    public ItemMap(short var1, short var2, int var3, int var4, int var5, int var6) {
        this.itemMapID = var1;
        this.template = ItemTemplates.gameAA(var2);
        this.x = var5;
        this.y = var4;
        this.xEnd = var5;
        this.yEnd = var6;
        this.vx = var5 - var3 >> 2;
        this.vy = 5;
    }

    public ItemMap(short var1, short var2, int var3, int var4) {
        this.itemMapID = var1;
        this.template = ItemTemplates.gameAA(var2);
        this.x = this.xEnd = var3;
        this.y = this.yEnd = var4;
        this.status = 1;
    }

    public final void gameAA(int var1, int var2) {
        this.xEnd = var1;
        this.yEnd = var2;
        this.vx = var1 - this.x >> 2;
        this.vy = var2 - this.y >> 2;
        this.status = 2;
    }
}
