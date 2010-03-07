package ui;

import bluetooth.Discovery;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import javax.bluetooth.ServiceRecord;
import javax.microedition.lcdui.*;
import main.Main;

public class ChooseDevice implements CommandListener {

//    private static final String noConns = "No one is connected";
    private List forma;
    static final Command options = new Command("Options", 4, 2);
    static final Command refresh = new Command("Refresh", 4, 1);
    static final Command about = new Command("About", 4, 3);
    static final Command chat = new Command("Chat", 4, 0);
    static final Command exit = new Command("Exit", 7, 1);
    public static final Ticker tick = new Ticker("Searching for contacts");
    private Hashtable chatForm;
    private String currentFriendName;
    private Vector completeNamesNicknames;
    private Image newMessage;
    private int command;
//    private String oldTicker;

    public ChooseDevice() {
        forma = new List("Online: ", 3);
        chatForm = new Hashtable();
        completeNamesNicknames = new Vector();
//        oldTicker = "";
        try {
            newMessage = Image.createImage("/newmsg.png");
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
        forma.setCommandListener(this);
    }

    public ChatForm getChatForm(String s) {
        if (s != null) {
            if (chatForm.size() == 0 || !chatForm.containsKey(s)) {
                ChatForm chatform = new ChatForm();
                chatform.buildBasic();
                chatForm.put(s, chatform);
                return chatform;
            } else {
                return (ChatForm) chatForm.get(s);
            }
        } else {
            return null;
        }
    }

    public void removeChatForm(String s) {
        if (s != null && chatForm.size() != 0 && chatForm.containsKey(s)) {
            chatForm.remove(s);
        }
    }

    public void cleanChatForm() {
        chatForm.clear();
    }

    public void newDevice(String s, ServiceRecord servicerecord) {
        if (s != null && !completeNamesNicknames.contains(s)) {
            if (forma.size() == 0 || forma.size() == 1 && forma.getString(0).equals("No one is connected")) {
                forma.deleteAll();
                forma.addCommand(chat);
                forma.addCommand(options);
                forma.addCommand(refresh);
                System.out.println("ADAUGATE!!");
            }
            completeNamesNicknames.addElement(s);
            completeNamesNicknames.addElement(s.substring(0, s.indexOf("_")));
            completeNamesNicknames.addElement(servicerecord);
            forma.append(s.substring(0, s.indexOf("_")), null);
        }
    }

    public void populateAndSetActive() {
        Vector vector = Discovery.getDevices();
        byte byte0 = -1;
        boolean flag = false;
        for (int i = completeNamesNicknames.size() / 3 - 1; i >= 0; i--) {
            if (!Main.existsClient((String) completeNamesNicknames.elementAt(i * 3)) && !vector.contains((ServiceRecord) completeNamesNicknames.elementAt(i * 3 + 2))) {
                completeNamesNicknames.removeElementAt(i * 3 + 2);
                completeNamesNicknames.removeElementAt(i * 3 + 1);
                completeNamesNicknames.removeElementAt(i * 3);
                forma.delete(i);
            }
        }

        int j = completeNamesNicknames.size() / 3;
        if (forma.size() != 0 && forma.getString(0).equals("No one is connected")) {
            forma.deleteAll();
        }
        for (int k = 0; k < vector.size(); k++) {
            String s = (String) vector.elementAt(k);
            if (!completeNamesNicknames.contains(s)) {
                completeNamesNicknames.addElement(s);
                completeNamesNicknames.addElement(s.substring(0, s.indexOf("_")));
                completeNamesNicknames.addElement(Discovery.getSelected(k));
                forma.append(s.substring(0, s.indexOf("_")), null);
            }
        }

        if (!flag && j != 0) {
            flag = true;
        }
        forma.setCommandListener(this);
        rearangeElements(null);
        if (vector.size() + j == 0) {
            forma.deleteAll();
            forma.append("No one is connected", null);
            forma.removeCommand(chat);
        } else {
            forma.addCommand(chat);
        }
        forma.addCommand(options);
        forma.addCommand(refresh);
        forma.addCommand(about);
        forma.addCommand(exit);
        if (forma.isShown()) {
            Main.getDisplay().setCurrent(forma);
            forma.setCommandListener(this);
        }
    }

    public boolean nameInList(String s) {
        if (s.indexOf(" wants to talk with you") != -1) {
            s = s.substring(0, s.indexOf(" wants to talk with you"));
            boolean flag = false;
            for (int i = 0; i < forma.size(); i++) {
                if (forma.getString(i).equals(s)) {
                    flag = true;
                }
            }

            return flag;
        } else {
            return false;
        }
    }

    public void commandAction(Command command1, Displayable displayable) {
        if (command1 == exit) {
            Main.getDiscover().cancel();
            Main.getDiscover().stop();
            Main.getServer().stopServer();
        } else if (command1 == chat) {
            currentFriendName = (String) completeNamesNicknames.elementAt(3 * forma.getSelectedIndex());
            getChatForm(currentFriendName).setFriend(currentFriendName);
            getChatForm(currentFriendName).setCurrent(completeNamesNicknames, forma);
        } else if (command1 == options) {
            Main.getOptionsForm().setActive();
        } else if (command1 == refresh) {
            Main.getDiscover().cancel();
            Main.getDiscover().go();
        } else if (command1 == about) {
            Main.getAboutForm().setActive();
        }
    }

    public void addToDisplayable(String s) {
        if (forma.getString(0).equals("No one is connected")) {
            forma.delete(0);
            forma.append(s.substring(0, s.indexOf("_")), null);
            completeNamesNicknames.addElement(s);
            completeNamesNicknames.addElement(s.substring(0, s.indexOf("_")));
            completeNamesNicknames.addElement(null);
        } else if (!completeNamesNicknames.contains(s)) {
            completeNamesNicknames.addElement(s);
            completeNamesNicknames.addElement(s.substring(0, s.indexOf("_")));
            completeNamesNicknames.addElement(null);
            forma.append(s.substring(0, s.indexOf("_")), null);
        }
        currentFriendName = s;
        getChatForm(currentFriendName).setFriend(currentFriendName);
    }

    public ChatForm getChatForm() {
        return getChatForm(currentFriendName);
    }

    public void addChatCommand() {
        forma.addCommand(chat);
    }

    public void addNewMessageIcon(String s) {
        int i = completeNamesNicknames.size();
        for (int j = 0; j < i; j += 3) {
            if (completeNamesNicknames.elementAt(j).equals(s)) {
                forma.set(j / 3, s.substring(0, s.indexOf("_")), newMessage);
            }
        }

    }

    public void removeNewMessageIcon(String s) {
        int i = completeNamesNicknames.size();
        for (int j = 0; j < i; j += 3) {
            if (completeNamesNicknames.elementAt(j).equals(s)) {
                forma.set(j / 3, s.substring(0, s.indexOf("_")), null);
            }
        }

    }

    public Displayable getForma() {
        return forma;
    }

    public void removeFriendFromList(String s) {
        int i = completeNamesNicknames.indexOf(s);
        if (i != -1) {
            completeNamesNicknames.removeElementAt(i + 2);
            completeNamesNicknames.removeElementAt(i + 1);
            completeNamesNicknames.removeElementAt(i);
            i /= 3;
            forma.delete(i);
            Main.removeClient(s);
            if (forma.size() == 0) {
                forma.append("No one is connected", null);
                forma.removeCommand(chat);
            }
        }
    }

    public void setCommand(int i) {
        command = i;
    }

    public int getCommand() {
        return command;
    }

    public void rearangeElements(Ticker ticker) {
        if (forma.size() != 0) {
            String as[] = new String[forma.size()];
            Image aimage[] = new Image[forma.size()];
            for (int i = 0; i < forma.size(); i++) {
                as[i] = forma.getString(i);
                aimage[i] = forma.getImage(i);
            }

            forma.deleteAll();
            forma.setTicker(ticker);
            for (int j = 0; j < as.length; j++) {
                forma.append(as[j], aimage[j]);
            }

        } else {
            forma.setTicker(ticker);
        }
    }

    public void initForma() {
        forma.addCommand(exit);
        formaVisible();
    }

    public void formaVisible() {
        Main.getDisplay().setCurrent(forma);
    }
}