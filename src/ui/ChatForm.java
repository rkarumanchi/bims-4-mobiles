package ui;

import java.util.Vector;
import javax.bluetooth.ServiceRecord;
import javax.microedition.lcdui.*;
import main.Main;

public class ChatForm implements CommandListener {

    private Form form;
    private Command chat;
    private Command back;
    private Command clear;
    private WriteMessageForm textEnterForm;
    private String friend;
    private boolean friendLeft;
    
    public ChatForm() {
        form = new Form("Chat");
        chat = new Command("Chat", 4, 0);
        back = new Command("Back", 2, 0);
        clear = new Command("Clear", 4, 1);
        friendLeft = false;
    }

    public void buildBasic() {
        form.addCommand(chat);
        form.addCommand(back);
        form.addCommand(clear);
        form.setCommandListener(this);
        textEnterForm = new WriteMessageForm(this);
    }

    private void repaint() {
        Alert alert = new Alert("Updating display ...", null, null, AlertType.INFO);
        alert.setTimeout(10);
        Main.getDisplay().setCurrent(alert, Main.getDisplay().getCurrent());
    }

    public void setCurrent(final Vector completeNamesNicknames, final List forma) {
        form.setTicker(null);
        Main.getDisplay().setCurrent(form);
        try {
            form.notify();
        } catch (Exception exception) {
        }
        scroll();
        if (completeNamesNicknames != null && forma != null) {
            Thread thread = new Thread() {

                public void run() {
                    Main.getClient((ServiceRecord) completeNamesNicknames.elementAt(3 * forma.getSelectedIndex() + 2), friend, true);
                    Main.getClient((ServiceRecord) completeNamesNicknames.elementAt(3 * forma.getSelectedIndex() + 2), friend, true).talk("");
                }
            };
            thread.start();
        }
    }

    public Form getForm() {
        return form;
    }

    public void scroll() {
        form.append(new StringItem("", null));
        Main.getDisplay().setCurrentItem(form.get(form.size() - 1));
    }

    public void setFriend(String s) {
        friend = s;
        textEnterForm.setFriend(friend);
    }

    public void addMsg(String s, String s1) {
        StringItem stringitem = new StringItem("", null);
        stringitem.setText(s + " \n");
        form.append(stringitem);
        if (!form.isShown() && s != null && !s.equals("")) {
            if (Main.getChooseDeviceForm().getForma().isShown()) {
                Main.getChooseDeviceForm().rearangeElements(new Ticker(friend.substring(0, friend.indexOf("_")) + " wants to talk with you"));
            } else {
                Main.getDisplay().getCurrent().setTicker(new Ticker(friend.substring(0, friend.indexOf("_")) + " wants to talk with you"));
            }
            Main.getChooseDeviceForm().addChatCommand();
            Main.getChooseDeviceForm().addNewMessageIcon(s1);
        }
    }

    public void removeCommandChat() {
        form.removeCommand(chat);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == chat) {
            textEnterForm.setCurrent();
        } else if (command == back) {
            Main.getChooseDeviceForm().removeNewMessageIcon(friend);
            Main.getChooseDeviceForm().rearangeElements(null);
            if (friendLeft) {
                Main.getChooseDeviceForm().removeFriendFromList(friend);
                Main.getChooseDeviceForm().removeChatForm(friend);
            }
            Main.getDisplay().setCurrent(Main.getChooseDeviceForm().getForma());
        } else if (command == clear) {
            form.deleteAll();
        }
    }

    public boolean isFormShown() {
        return form.isShown();
    }

    public void setFriendLeft() {
        friendLeft = true;
    }

    public boolean getFriendLeft() {
        return friendLeft;
    }
    
}