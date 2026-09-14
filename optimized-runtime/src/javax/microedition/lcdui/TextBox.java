package javax.microedition.lcdui;

public class TextBox extends Displayable {
    private final String title;
    private String text;
    private int maxSize;
    private int constraints;
    private CommandListener listener;

    public TextBox(String title, String text, int maxSize, int constraints) {
        this.title = title;
        this.text = text == null ? "" : text;
        this.maxSize = maxSize;
        this.constraints = constraints;
    }

    public void addCommand(Command command) {
    }

    public void setCommandListener(CommandListener listener) {
        this.listener = listener;
    }

    public void setConstraints(int constraints) {
        this.constraints = constraints;
    }

    public void setString(String text) {
        this.text = text == null ? "" : text;
    }

    public String getString() {
        return text;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public String getTitle() {
        return title;
    }
}
