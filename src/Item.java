
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.microedition.lcdui.Image;

public final class Item {

    public int indexFrame;
    public ItemTemplate template;
    public MyVector options;
    public int itemId;
    public int playerId;
    public int indexUI;
    public int quantity;
    public long expires;
    public boolean isLock;
    public int sys;
    public int upgrade;
    public int buyCoin;
    public int buyCoinLock;
    public int buyGold;
    private int buyGoldLock;
    public int saleCoinLock;
    public int typeUI;
    public boolean isExpires;
    public EffectCharPaint eff;
    public int indexEff;
    public Image img;
    public boolean fieldAS = false;
    public long fieldAT = 0L;
    public long fieldAU = 0L;
    public boolean fieldAV = false;
    public boolean fieldAW = false;
    public int fieldAX = 0;
    public long fieldAY = 0L;

    public final Item clone() {
        Item var1;
        (var1 = new Item()).template = this.template;
        if (this.options != null) {
            var1.options = new MyVector();

            for (int var2 = 0; var2 < this.options.size(); ++var2) {
                ItemOption var3;
                (var3 = new ItemOption()).optionTemplate = ((ItemOption) this.options.elementAt(var2)).optionTemplate;
                var3.param = ((ItemOption) this.options.elementAt(var2)).param;
                var1.options.addElement(var3);
            }
        }

        var1.itemId = this.itemId;
        var1.playerId = this.playerId;
        var1.indexUI = this.indexUI;
        var1.quantity = this.quantity;
        var1.expires = this.expires;
        var1.isLock = this.isLock;
        var1.sys = this.sys;
        var1.upgrade = this.upgrade;
        var1.buyCoin = this.buyCoin;
        var1.buyCoinLock = this.buyCoinLock;
        var1.buyGold = this.buyGold;
        var1.buyGoldLock = this.buyGoldLock;
        var1.saleCoinLock = this.saleCoinLock;
        var1.typeUI = this.typeUI;
        var1.isExpires = this.isExpires;
        return var1;
    }

    public final Item viewNext(int var1) {
        Item var4;
        (var4 = this.clone()).isLock = true;
        int var2;
        if ((var2 = var1 - var4.upgrade) == 0) {
            return var4;
        } else {
            var4.upgrade = var1;
            if (var4.options != null) {
                for (var1 = 0; var1 < var4.options.size(); ++var1) {
                    ItemOption var3;
                    if ((var3 = (ItemOption) var4.options.elementAt(var1)).optionTemplate.id != 6 && var3.optionTemplate.id != 7) {
                        if (var3.optionTemplate.id != 8 && var3.optionTemplate.id != 9 && var3.optionTemplate.id != 19) {
                            if (var3.optionTemplate.id != 10 && var3.optionTemplate.id != 11 && var3.optionTemplate.id != 12 && var3.optionTemplate.id != 13 && var3.optionTemplate.id != 14 && var3.optionTemplate.id != 15 && var3.optionTemplate.id != 17 && var3.optionTemplate.id != 18 && var3.optionTemplate.id != 20) {
                                if (var3.optionTemplate.id != 21 && var3.optionTemplate.id != 22 && var3.optionTemplate.id != 23 && var3.optionTemplate.id != 24 && var3.optionTemplate.id != 25 && var3.optionTemplate.id != 26) {
                                    if (var3.optionTemplate.id == 16) {
                                        var3.param += var2 * 3;
                                    }
                                } else {
                                    var3.param += var2 * 150;
                                }
                            } else {
                                var3.param += var2 * 5;
                            }
                        } else {
                            var3.param += var2 * 10;
                        }
                    } else {
                        var3.param += var2 * 15;
                    }
                }
            }

            return var4;
        }
    }

    public final boolean isTypeBody() {
        return this.template.type >= 0 && this.template.type <= 15;
    }

    public final boolean isTypeMounts() {
        return 29 <= this.template.type && this.template.type <= 33;
    }

    public final boolean isTypeNgocKham() {
        return this.template.type == 34;
    }

    public final String getExpiresString() {
        if (this.expires <= 0L) {
            return mResources.gameFK;
        } else {
            Calendar var1;
            (var1 = Calendar.getInstance()).setTimeZone(TimeZone.getTimeZone("GMT+7"));
            var1.setTime(new Date(this.expires));
            int var5 = var1.get(11);
            int var2 = var1.get(12);
            int var3 = var1.get(5);
            int var4 = var1.get(2) + 1;
            int var6 = var1.get(1);
            return var3 + "/" + var4 + "/" + var6 + " " + var5 + "h" + var2 + "'";
        }
    }

    public final String getExpiresShopString() {
        if (this.expires <= 0L) {
            return mResources.gameFK;
        } else if (this.expires / 1000L >= 2592000L) {
            return this.expires / 1000L / 2592000L + " " + mResources.gamePU;
        } else if (this.expires / 1000L >= 604800L) {
            return this.expires / 1000L / 604800L + " " + mResources.gamePV;
        } else if (this.expires / 1000L >= 86400L) {
            return this.expires / 1000L / 86400L + " " + mResources.gamePW;
        } else if (this.expires / 1000L >= 3600L) {
            return this.expires / 1000L / 3600L + " " + mResources.gamePX;
        } else {
            return this.expires / 1000L >= 60L ? this.expires / 1000L / 60L + " " + mResources.gamePZ : "";
        }
    }

    public final void clearExpire() {
        if (!this.isTypeMounts()) {
            this.expires = 0L;
        }

    }

    public final boolean isTypeUIMe() {
        return this.typeUI == 5 || this.typeUI == 3 || this.typeUI == 4 || this.typeUI == 39;
    }

    public final boolean isTypeUIShopView() {
        if (this.isTypeUIShop()) {
            return true;
        } else {
            return this.isTypeUIStore() || this.isTypeUIBook() || this.isTypeUIFashion() || this.isTypeUIClanShop();
        }
    }

    public final boolean isTypeUIShop() {
        return this.typeUI == 20 || this.typeUI == 21 || this.typeUI == 22 || this.typeUI == 23 || this.typeUI == 24 || this.typeUI == 25 || this.typeUI == 26 || this.typeUI == 27 || this.typeUI == 28 || this.typeUI == 29 || this.typeUI == 16 || this.typeUI == 17 || this.typeUI == 18 || this.typeUI == 19 || this.typeUI == 2 || this.typeUI == 6 || this.typeUI == 8 || this.typeUI == 34;
    }

    public final boolean isTypeUIShopLock() {
        return this.typeUI == 7 || this.typeUI == 9;
    }

    public final boolean isTypeUIStore() {
        return this.typeUI == 14;
    }

    public final boolean isTypeUIBook() {
        return this.typeUI == 15;
    }

    public final boolean isTypeUIFashion() {
        return this.typeUI == 32;
    }

    public final boolean isTypeUIClanShop() {
        return this.typeUI == 34;
    }

    public final boolean isUpMax() {
        return this.getUpMax() == this.upgrade;
    }

    public final int getUpMax() {
        if (this.template.level >= 1 && this.template.level < 20) {
            return 4;
        } else if (this.template.level >= 20 && this.template.level < 40) {
            return 8;
        } else if (this.template.level >= 40 && this.template.level < 50) {
            return 12;
        } else {
            return this.template.level >= 50 && this.template.level < 60 ? 14 : 16;// cũ 16
        }
    }

    public final boolean isTypeClothe() {
        return this.template.type == 0 || this.template.type == 2 || this.template.type == 4 || this.template.type == 6 || this.template.type == 8;
    }

    public final boolean isTypeAdorn() {
        return this.template.type == 3 || this.template.type == 5 || this.template.type == 7 || this.template.type == 9;
    }

    public final boolean isTypeWeapon() {
        return this.template.type == 1;
    }

    public final boolean isNewitem() {
        if (this.options != null) {
            for (int var1 = 0; var1 < this.options.size(); ++var1) {
                if (((ItemOption) this.options.elementAt(var1)).optionTemplate.id == 155) {
                    return true;
                }
            }
        }

        return false;
    }

    public final int fieldAU() {
        if (this.options == null || this.options.size() == 0) {
            return -1;
        }

        for (int i = 0; i < this.options.size(); i++) {
            ItemOption option = (ItemOption) this.options.elementAt(i);
            if (option != null && option.optionTemplate.id == 85) {
                return option.param;
            }
        }

        return -1;
    }


    public final boolean fieldAB(int var1) {
        if (this.options != null) {
            for (int var2 = 0; var2 < this.options.size(); ++var2) {
                ItemOption var3;
                if ((var3 = (ItemOption) this.options.elementAt(var2)) != null && var3.optionTemplate.id == var1) {
                    return true;
                }
            }
        }

        return false;
    }

    public final int fieldAC(int var1) {
        int var2 = 0;
        if (this.options != null) {
            for (int var3 = 0; var3 < this.options.size(); ++var3) {
                ItemOption var4;
                if ((var4 = (ItemOption) this.options.elementAt(var3)) != null && var4.optionTemplate.type == var1) {
                    ++var2;
                }
            }
        }

        return var2;
    }
}
