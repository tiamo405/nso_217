package javax.microedition.lcdui;

public class Command {
    private final String label;
    private final int type;
    private final int priority;

    public Command(String label, int type, int priority) {
        this.label = label;
        this.type = type;
        this.priority = priority;
    }

    public String getLabel() {
        return label;
    }

    public int getCommandType() {
        return type;
    }

    public int getPriority() {
        return priority;
    }
}
