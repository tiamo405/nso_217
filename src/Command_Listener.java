
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;

final class Command_Listener implements CommandListener {

    private TField gameAA;
    private final TextBox gameAB;

    Command_Listener(TField var1, TextBox var2) {
        this.gameAA = var1;
        this.gameAB = var2;
    }

    public final void commandAction(javax.microedition.lcdui.Command var1, Displayable var2) {
        if (var1.getLabel().equals(mResources.gameCX)) {
            this.gameAA.setText(this.gameAB.getString());
        }

        Display.getDisplay(TField.gameAJ).setCurrent(TField.gameAI);
        TField.gameAI.setFullScreenMode(true);
        this.gameAA.fieldAI = true;

    }
}
