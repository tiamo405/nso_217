
public final class MoAll implements Runnable {

    private final Item fieldAA;

    MoAll(Item var1) {
        this.fieldAA = var1;
    }

    public final void run() {
        try {
            int quantityToProcess = this.fieldAA.quantity;

            for (int var1 = 0; var1 < quantityToProcess && Char.fieldBF() > 3; ++var1) {
                Service.gI().useItem(this.fieldAA.indexUI);
                Thread.sleep(15L);
            }
        } catch (InterruptedException var2) {
        }

    }
}
