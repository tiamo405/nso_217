
import java.util.Calendar;

public final class TanSat extends Auto {

    public int fieldAV;
    public static int mapid = 134;

    public final void fieldAA(int var1, int var2, int var3) {
        super.fieldAD();
        super.fieldAB = var2;
        super.fieldAC = var3;
        super.fieldAD = TileMap.isHang(var2);
        this.fieldAV = var1;
    }

    public final void fieldAA() {
        if (super.fieldAB == 134 || super.fieldAB == 135 || super.fieldAB == 136 || super.fieldAB == 137) {
            mapid = super.fieldAB;
        }
        if (Auto.fieldAF()) {
            if (Char.fieldEW) {
                Auto.fieldAA(true);
            }
        } else {
            Calendar var1;
            int var2 = (var1 = Res.fieldAB()).get(11);
            int var3 = var1.get(12);

            if (!Auto.fieldAK && Char.getMyChar().isHuman) {
                this.fieldAK();
                return;
            }

            if (super.fieldAB != TileMap.mapID || !super.fieldAD && super.fieldAC != TileMap.zoneID) {
                this.fieldAA(super.fieldAB, super.fieldAC, super.fieldAE, super.fieldAF);
            } else {
                if (Char.fieldEM && Code.fieldAJ() && Char.fieldBF() < 5 && !TileMap.isLangCo(TileMap.mapID)) {
                    Auto.fieldAH();
                    return;
                }

                this.fieldAB(this.fieldAV, this.fieldAA(Char.fieldFD, Char.fieldFE, Char.fieldFF));
                this.fieldAC(-1);
            }
        }

    }

    public final String toString() {
        return this.fieldAV >= 0 && this.fieldAV < Mob.arrMobTemplate.length
                ? "Tàn sát " + Mob.arrMobTemplate[this.fieldAV].name
                : "Tàn sát ";
    }
}
