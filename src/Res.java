
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

public final class Res {

    private static short[] SIN = new short[]{0, 18, 36, 54, 71, 89, 107, 125, 143, 160, 178, 195, 213, 230, 248, 265, 282, 299, 316, 333, 350, 367, 384, 400, 416, 433, 449, 465, 481, 496, 512, 527, 543, 558, 573, 587, 602, 616, 630, 644, 658, 672, 685, 698, 711, 724, 737, 749, 761, 773, 784, 796, 807, 818, 828, 839, 849, 859, 868, 878, 887, 896, 904, 912, 920, 928, 935, 943, 949, 956, 962, 968, 974, 979, 984, 989, 994, 998, 1002, 1005, 1008, 1011, 1014, 1016, 1018, 1020, 1022, 1023, 1023, 1024, 1024};
    private static short[] COS;
    private static int[] TAN;
    private static Random r = new Random();

    public static Calendar fieldAB() {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT+7"));
    }

    public static void gameAA() {
        COS = new short[91];
        TAN = new int[91];

        for (int var0 = 0; var0 <= 90; ++var0) {
            COS[var0] = SIN[90 - var0];
            if (COS[var0] == 0) {
                TAN[var0] = Integer.MAX_VALUE;
            } else {
                TAN[var0] = (SIN[var0] << 10) / COS[var0];
            }
        }

    }

    public static final int gameAA(int var0) {
        if ((var0 = gameAC(var0)) >= 0 && var0 < 90) {
            return SIN[var0];
        } else if (var0 >= 90 && var0 < 180) {
            return SIN[180 - var0];
        } else {
            return var0 >= 180 && var0 < 270 ? -SIN[var0 - 180] : -SIN[360 - var0];
        }
    }

    public static final int gameAB(int var0) {
        if ((var0 = gameAC(var0)) >= 0 && var0 < 90) {
            return COS[var0];
        } else if (var0 >= 90 && var0 < 180) {
            return -COS[180 - var0];
        } else {
            return var0 >= 180 && var0 < 270 ? -COS[var0 - 180] : COS[360 - var0];
        }
    }

    public static final int fieldAA(int var0, int var1) {
        if (var0 == 0) {
            return var1 > 0 ? 90 : 270;
        } else {
            int var2 = Math.abs((var1 << 10) / var0);
            int var3 = 0;

            while (true) {
                if (var3 > 90) {
                    var2 = 0;
                    break;
                }

                if (TAN[var3] >= var2) {
                    var2 = var3;
                    break;
                }

                ++var3;
            }

            if (var1 >= 0 && var0 < 0) {
                var2 = 180 - var2;
            }

            if (var1 < 0 && var0 < 0) {
                var2 += 180;
            }

            return var1 < 0 && var0 >= 0 ? 360 - var2 : var2;
        }
    }

    public static final int gameAA(int var0, int var1) {
        int var10000;
        int var2;
        if (var0 != 0) {
            var2 = Math.abs((var1 << 10) / var0);

            label44:
            {
                for (int var3 = 0; var3 <= 90; ++var3) {
                    if (TAN[var3] >= var2) {
                        var10000 = var3;
                        break label44;
                    }
                }

                var10000 = 0;
            }

            var2 = var10000;
            if (var1 >= 0 && var0 < 0) {
                var2 = 180 - var2;
            }

            if (var1 < 0 && var0 < 0) {
                var2 += 180;
            }

            if (var1 >= 0 || var0 < 0) {
                return var2;
            }

            var10000 = 360 - var2;
        } else {
            var10000 = var1 > 0 ? 90 : 270;
        }

        var2 = var10000;
        return var2;
    }

    public static final int gameAC(int var0) {
        if (var0 >= 360) {
            var0 -= 360;
        }

        if (var0 < 0) {
            var0 += 360;
        }

        return var0;
    }

    public static int gameAB(int var0, int var1) {
        return gameAB(gameAC(var0)) * var1 >> 10;
    }

    public static int gameAC(int var0, int var1) {
        return gameAA(gameAC(var0)) * var1 >> 10;
    }

    public static int gameAD(int var0, int var1) {
        return var0 + r.nextInt(var1 - var0);
    }

    public static int gameAA(int var0, int var1, int var2, int var3) {
        if ((var0 = (var0 - var2) * (var0 - var2) + (var1 - var3) * (var1 - var3)) <= 0) {
            return 0;
        } else {
            var1 = (var0 + 1) / 2;

            do {
                var2 = var1;
                var1 = var1 / 2 + var0 / (var1 * 2);
            } while (Math.abs(var2 - var1) > 1);

            return var1;
        }
    }

    public static int gameAD(int var0) {
        return r.nextInt(var0);
    }

    public static int abs(int var0) {
        return var0 > 0 ? var0 : -var0;
    }

    public static void gameAA(mGraphics var0) {
        var0.gameAA(-var0.gameAA(), -var0.gameAB());
        var0.gameAD(0, 0, GameCanvas.w, GameCanvas.h);
    }

    public static String[] split(String var0, String var1, int var2) {
        int var3;
        String[] var4;
        if ((var3 = var0.indexOf(var1)) >= 0) {
            var4 = split(var0.substring(var3 + var1.length()), var1, var2 + 1);
        } else {
            var4 = new String[var2 + 1];
            var3 = var0.length();
        }

        var4[var2] = var0.substring(0, var3);
        return var4;
    }

    public static String gameAA(long var0, int var2) {
        String var3 = "";
        long var4;
        if ((var4 = (var0 + (long) var2 - System.currentTimeMillis()) / 1000L) <= 0L) {
            return "";
        } else {
            long var6 = 0L;
            long var8 = 0L;
            long var10 = var4 / 60L;
            long var12 = var4;
            if (var4 >= 86400L) {
                var6 = var4 / 86400L;
                var8 = var4 % 86400L / 3600L;
            } else if (var4 >= 3600L) {
                var8 = var4 / 3600L;
                var10 = var4 % 3600L / 60L;
            } else if (var4 >= 60L) {
                var10 = var4 / 60L;
                var12 = var4 % 60L;
            } else {
                var12 = var4;
            }

            String var10000;
            if (var6 > 0L) {
                if (var6 >= 10L) {
                    var10000 = var8 < 1L ? var6 + "d" : (var8 < 10L ? var6 + "d" + "0" + var8 + "h" : var6 + "d" + var8 + "h");
                } else {
                    if (var6 >= 10L) {
                        return var3;
                    }

                    var10000 = var8 < 1L ? var6 + "d" : (var8 < 10L ? var6 + "d" + "0" + var8 + "h" : var6 + "d" + var8 + "h");
                }
            } else if (var8 > 0L) {
                if (var8 >= 10L) {
                    var10000 = var10 < 1L ? var8 + "h" : (var10 < 10L ? var8 + "h" + "0" + var10 + "m" : var8 + "h" + var10 + "m");
                } else {
                    if (var8 >= 10L) {
                        return var3;
                    }

                    var10000 = var10 < 1L ? var8 + "h" : (var10 < 10L ? var8 + "h" + "0" + var10 + "m" : var8 + "h" + var10 + "m");
                }
            } else if (var10 > 0L) {
                if (var10 >= 10L) {
                    if (var12 >= 10L) {
                        var10000 = var10 + "m" + var12 + "s";
                    } else {
                        if (var12 >= 10L) {
                            return var3;
                        }

                        var10000 = var10 + "m" + "0" + var12 + "s";
                    }
                } else {
                    if (var10 >= 10L) {
                        return var3;
                    }

                    if (var12 >= 10L) {
                        var10000 = var10 + "m" + var12 + "s";
                    } else {
                        if (var12 >= 10L) {
                            return var3;
                        }

                        var10000 = var10 + "m" + "0" + var12 + "s";
                    }
                }
            } else {
                var10000 = var12 < 10L ? "0" + var12 + "s" : var12 + "s";
            }

            var3 = var10000;
            return var3;
        }
    }

    public static DataInputStream fieldAA(String var0) {
        DataInputStream var1 = null;
        InputStream var2;
        if ((var2 = "".getClass().getResourceAsStream(var0)) != null) {
            var1 = new DataInputStream(var2);
        }

        return var1;
    }

    public static boolean fieldAA(int var0, int var1, int var2, int var3, int var4, int var5) {
        return var0 >= var2 && var0 < var2 + var4 && var1 >= var3 && var1 < var3 + var5;
    }

    public static int fieldAF(int var0) {
        if (var0 <= 0) {
            return 0;
        } else {
            int var1 = (var0 + 1) / 2;

            while (true) {
                int var2 = var1 / 2 + var0 / (var1 << 1);
                if (Math.abs(var1 - var2) <= 1) {
                    return var2;
                }

                var1 = var2;
            }
        }
    }

}
