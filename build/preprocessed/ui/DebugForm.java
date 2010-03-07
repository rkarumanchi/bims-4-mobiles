package ui;

import javax.microedition.lcdui.*;
import main.Main;

public class DebugForm implements CommandListener {

    Form debugf;
    Command exit;
    Displayable parent;

    public DebugForm(String s, String s1, Displayable displayable) {
        exit = new Command("Exit", 7, 1);
        debugf = new Form(s);
        debugf.append(new StringItem(s1, ""));
        debugf.addCommand(exit);
        parent = displayable;
        debugf.setCommandListener(this);
        Main.getDisplay().setCurrent(debugf);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == exit) {
            Main.getDisplay().setCurrent(parent);
        }
    }
}