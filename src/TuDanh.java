
public final class TuDanh extends Auto {

    public final void fieldAD() {
        super.fieldAD();
        super.fieldAB = TileMap.mapID;
        super.fieldAC = TileMap.zoneID;
        super.fieldAE = Char.getMyChar().cx;
        super.fieldAF = Char.getMyChar().cy;
    }

    public final void fieldAA() {
        if (super.fieldAB == 134 || super.fieldAB == 135 || super.fieldAB == 136 || super.fieldAB == 137) {
            TanSat.mapid = super.fieldAB;
        }
        if (Auto.fieldAF()) {
            if (Char.fieldEW) {
                Auto.fieldAA(true);
                return;
            }
        } else {
            if (!Auto.fieldAK && Char.getMyChar().isHuman) {
                this.fieldAK();
                return;
            }

            if (super.fieldAB == TileMap.mapID && (super.fieldAD || super.fieldAC == TileMap.zoneID)) {
                this.fieldAC(-1);
                this.fieldAB(-1, -1);
                return;
            }

            if (Char.fieldEW) {
                this.fieldAA(super.fieldAB, super.fieldAC, super.fieldAE, super.fieldAF);
            }
        }

    }

    protected final Mob fieldAA(Char var1, int var2, int var3, Char var4, boolean var5) {
        if (Code.fieldAR && Code.fieldAT.size() > 0) {
            this.fieldAA(var3, var5);
            return Auto.fieldAA(var1.cx, var1.cy);
        } else {
            return Auto.fieldAA(var1.cx, var1.cy);
        }
    }

    public final String toString() {
        return "Tự đánh";
    }
}
