
public final class AutoNpc implements Runnable {

    private final int fieldAA;
    private final int fieldAB;
    private final int fieldAC;

    AutoNpc(int var1, int var2, int var3) {
        this.fieldAA = var1;
        this.fieldAB = var2;
        this.fieldAC = var3;
    }

    public final void run() {
        GameScr.fieldAH(33);
        Service.gI().openMenu(33);

        for (int var1 = 0; var1 < this.fieldAA; ++var1) {
            Service.gI().menu((byte) 0, 33, this.fieldAB, this.fieldAC);

            try {
                Thread.sleep(20L);
            } catch (InterruptedException var2) {
            }
        }

    }
}
