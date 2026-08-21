
public final class LockGame {

    private static boolean fieldAC = false;
    public static boolean fieldAA = false;
    public static boolean fieldAB = false;
    private static boolean fieldAD = false;
    private static boolean fieldAE = false;
    private static boolean fieldAF = false;
    private static boolean fieldAG = false;
    private static boolean fieldAH = false;
    private static boolean fieldAI = false;
    private static boolean fieldAJ = false;
    private static boolean fieldAK = false;
    private static boolean fieldAL = false;
    private static boolean fieldAM = false;
    private static boolean fieldAN = false;
    private static boolean fieldAO = false;
    private static boolean fieldAP = false;
    private static boolean fieldAQ = false;
    private static Object fieldAR = new Object();
    private static int fieldAS;

    public static void fieldAA() {
        fieldAC = true;
        synchronized (fieldAR) {
            try {
                fieldAR.wait(2000L);
            } catch (InterruptedException var1) {
            }

        }
    }

    public static void fieldAB() {
        if (fieldAC) {
            synchronized (fieldAR) {
                fieldAR.notifyAll();
            }

            fieldAC = false;
        }

    }

    public static boolean lockpick() {
        fieldAB = true;
        long var0 = System.currentTimeMillis();
        synchronized (fieldAR) {
            try {
                fieldAR.wait(500L);
            } catch (InterruptedException var3) {
            }
        }

        return System.currentTimeMillis() - var0 < 1L;
    }

    public static boolean fieldAC() {
        fieldAB = true;
        long var0 = System.currentTimeMillis();
        synchronized (fieldAR) {
            try {
                fieldAR.wait(500L);
            } catch (InterruptedException var3) {
            }
        }

        return System.currentTimeMillis() - var0 < 500L;
    }

    public static void fieldAD() {
        if (fieldAB) {
            synchronized (fieldAR) {
                fieldAR.notifyAll();
            }

            fieldAB = false;
        }

    }

    public static void fieldAE() {
        fieldAD = true;
        synchronized (fieldAR) {
            try {
                fieldAR.wait(2000L);
            } catch (InterruptedException var1) {
            }

        }
    }

    public static void fieldAF() {
        if (fieldAD) {
            synchronized (fieldAR) {
                fieldAR.notifyAll();
            }

            fieldAD = false;
        }

    }

    public static void fieldAG() {
        fieldAE = true;
        synchronized (fieldAR) {
            try {
                fieldAR.wait(2000L);
            } catch (InterruptedException var1) {
            }

        }
    }

    public static void fieldAH() {
        if (fieldAE) {
            synchronized (fieldAR) {
                fieldAR.notifyAll();
            }

            fieldAE = false;
        }

    }

    public static void fieldAI() {
        fieldAJ = true;
        synchronized (fieldAR) {
            try {
                fieldAR.wait();
            } catch (InterruptedException var1) {
            }

        }
    }

    public static void fieldAJ() {
        if (fieldAJ) {
            synchronized (fieldAR) {
                fieldAR.notifyAll();
            }

            fieldAJ = false;
        }

    }

    public static void fieldAK() {
        fieldAF = true;
        synchronized (fieldAR) {
            try {
                fieldAR.wait(2000L);
            } catch (InterruptedException var1) {
            }

        }
    }

    public static void fieldAL() {
        if (fieldAF) {
            synchronized (fieldAR) {
                fieldAR.notifyAll();
            }

            fieldAF = false;
        }

    }

    public static void fieldAM() {
        fieldAG = true;
        synchronized (fieldAR) {
            try {
                fieldAR.wait(2000L);
            } catch (InterruptedException var1) {
            }

        }
    }

    public static void fieldAN() {
        if (fieldAG) {
            synchronized (fieldAR) {
                fieldAR.notifyAll();
            }

            fieldAG = false;
        }

    }

    public static void fieldAO() {
        fieldAH = true;
        synchronized (fieldAR) {
            try {
                fieldAR.wait(2000L);
            } catch (InterruptedException var1) {
            }

        }
    }

    public static void fieldAP() {
        if (fieldAH) {
            synchronized (fieldAR) {
                fieldAR.notifyAll();
            }

            fieldAH = false;
        }

    }

    public static boolean fieldAQ() {
        fieldAI = true;
        long var0 = System.currentTimeMillis();
        synchronized (fieldAR) {
            try {
                fieldAR.wait(2000L);
            } catch (InterruptedException var3) {
            }
        }

        return System.currentTimeMillis() - var0 < 2000L;
    }

    public static void fieldAR() {
        if (fieldAI) {
            synchronized (fieldAR) {
                fieldAR.notifyAll();
            }

            fieldAI = false;
        }

    }

    public static boolean fieldAS() {
        fieldAK = true;
        long var0 = System.currentTimeMillis();
        synchronized (fieldAR) {
            try {
                fieldAR.wait(7000L);
            } catch (InterruptedException var3) {
            }
        }

        return System.currentTimeMillis() - var0 < 7000L;
    }

    public static void fieldAT() {
        if (fieldAK) {
            synchronized (fieldAR) {
                fieldAR.notifyAll();
            }

            fieldAK = false;
        }

    }

    public static boolean fieldAU() {
        fieldAS = Auto.fieldAL != null ? Auto.fieldAL.point : 0;
        fieldAL = true;
        synchronized (fieldAR) {
            try {
                fieldAR.wait(3000L);
            } catch (InterruptedException var1) {
            }
        }

        return Auto.fieldAL == null || Auto.fieldAL.point > fieldAS;
    }

    public static void fieldAV() {
        if (fieldAL) {
            synchronized (fieldAR) {
                fieldAR.notifyAll();
            }

            fieldAL = false;
        }

    }

    public static void fieldAW() {
        fieldAM = true;
        synchronized (fieldAR) {
            try {
                fieldAR.wait(3000L);
            } catch (InterruptedException var1) {
            }

        }
    }

    public static void fieldAX() {
        if (fieldAM) {
            synchronized (fieldAR) {
                fieldAR.notifyAll();
            }

            fieldAM = false;
        }

    }

    public static boolean fieldAY() {
        fieldAN = true;
        long var0 = System.currentTimeMillis();
        synchronized (fieldAR) {
            try {
                fieldAR.wait(5000L);
            } catch (InterruptedException var3) {
            }
        }

        return System.currentTimeMillis() - var0 < 5000L;
    }

    public static void fieldAZ() {
        if (fieldAN) {
            synchronized (fieldAR) {
                fieldAR.notifyAll();
            }

            fieldAN = false;
        }

    }

    public static boolean fieldBA() {
        fieldAO = true;
        if (20000L < 0L) {
            synchronized (fieldAR) {
                try {
                    fieldAR.wait();
                } catch (InterruptedException var4) {
                }
            }

            return true;

        } else {
            long var2 = System.currentTimeMillis();
            synchronized (fieldAR) {
                try {
                    fieldAR.wait(20000L);
                } catch (InterruptedException var6) {
                }
            }

            return System.currentTimeMillis() - var2 < 20000L;

        }
    }

    public static void fieldBB() {
        if (fieldAO) {
            synchronized (fieldAR) {
                fieldAR.notifyAll();
            }

            fieldAO = false;
        }

    }

    public static void fieldBC() {
        fieldAP = true;
        synchronized (fieldAR) {
            try {
                fieldAR.wait(500L);
            } catch (InterruptedException var1) {
            }

        }
    }

    public static void fieldBD() {
        if (fieldAP) {
            synchronized (fieldAR) {
                fieldAR.notifyAll();
            }

            fieldAP = false;
        }

    }

    public static boolean fieldBE() {
        System.out.println("WaitDV");
        fieldAQ = true;
        long var0 = System.currentTimeMillis();
        synchronized (fieldAR) {
            try {
                fieldAR.wait(10000L);
            } catch (InterruptedException var3) {
            }
        }

        return System.currentTimeMillis() - var0 < 10000L;
    }

    public static void fieldBF() {
        System.out.println("NotifyDV");
        if (fieldAQ) {
            synchronized (fieldAR) {
                fieldAR.notifyAll();
            }

            fieldAQ = false;
        }

    }

    public static boolean fieldBG() {
        int var0 = 0;

        label31:
        while (var0 < Code.fieldAI.size()) {
            String var1 = (String) Code.fieldAI.elementAt(var0);

            for (int var2 = 0; var2 < GameScr.vParty.size(); ++var2) {
                Party var3;
                if ((var3 = (Party) GameScr.vParty.elementAt(var2)).name.equals(var1)) {
                    if (var3.c == null || Res.gameAA(Char.getMyChar().cx, Char.getMyChar().cy, var3.c.cx, var3.c.cy) > 100) {
                        return false;
                    }

                    ++var0;
                    continue label31;
                }
            }

            return false;
        }

        return true;
    }

    public static boolean fieldBH() {
        label23:
        for (int var0 = 0; var0 < Code.fieldAI.size(); ++var0) {
            String var1 = (String) Code.fieldAI.elementAt(var0);

            for (int var2 = 0; var2 < GameScr.vParty.size(); ++var2) {
                if (((Party) GameScr.vParty.elementAt(var2)).name.equals(var1)) {
                    continue label23;
                }
            }

            return false;
        }

        return true;
    }

    public static void fieldAA(long var0) {
        if (Code.fieldAH != null) {
            long var2 = System.currentTimeMillis();

            while (!fieldBG() && System.currentTimeMillis() - var2 < var0) {
                cuong.sleep(2000L);
            }

        }
    }

    public static void fieldBI() {
        if (Code.fieldAH != null) {
            long var2 = System.currentTimeMillis();

            while (true) {
                int var0 = 0;

                boolean var10000;
                label37:
                while (true) {
                    if (var0 >= Code.fieldAI.size()) {
                        var10000 = true;
                        break;
                    }

                    String var1 = (String) Code.fieldAI.elementAt(var0);

                    for (int var4 = 0; var4 < GameScr.vParty.size(); ++var4) {
                        Party var5;
                        if ((var5 = (Party) GameScr.vParty.elementAt(var4)).name.equals(var1)) {
                            if (var5.c == null) {
                                var10000 = false;
                                break label37;
                            }

                            ++var0;
                            continue label37;
                        }
                    }

                    var10000 = false;
                    break;
                }

                if (var10000 || System.currentTimeMillis() - var2 >= 300000L) {
                    return;
                }

                cuong.sleep(2000L);
            }
        }
    }

    public static void fieldBJ() {
        if (Code.fieldAH != null) {
            long var0 = System.currentTimeMillis();

            while (!fieldBH() && System.currentTimeMillis() - var0 < 60000L) {
                cuong.sleep(2000L);
            }

        }
    }

    public static void fieldBK() {
        synchronized (fieldAR) {
            fieldAR.notifyAll();
        }

        fieldAB = false;
        fieldAD = false;
        fieldAE = false;
        fieldAF = false;
        fieldAG = false;
        fieldAH = false;
        fieldAJ = false;
        fieldAC = false;
        fieldAI = false;
        fieldAA = false;
        fieldAK = false;
        fieldAL = false;
        fieldAM = false;
        fieldAN = false;
        fieldAO = false;
        fieldAP = false;
        TileMap.fieldBE = false;
        TileMap.fieldAG();
    }
}
