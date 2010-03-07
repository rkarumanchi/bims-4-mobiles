package ui;

import bluetooth.TimerRefresh;
import java.util.Timer;
import javax.microedition.lcdui.*;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import main.Main;

public class OptionsForm implements CommandListener {

    private Form options;
    private Command ok;
    private Command back;
    private TextField f_nickname_ok;
    private ChoiceGroup alert;
    private ChoiceGroup refreshRate;
    private String lastNameOfServer;
    public static String nickname = "";
    public static String alertSound = "false";
    public static String vibrate = "false";
    public static int refreshIndex = 0;
    private int refresh[] = {
        0, 30000, 60000, 0x2bf20
    };
    private boolean dataExists;
    private Timer timerRefresh;
    private Alert mustRestart;
    
    public OptionsForm() {
        options = new Form("Options");
        ok = new Command("Ok", 4, 0);
        back = new Command("Back", 2, 0);
        f_nickname_ok = new TextField("Nickname", "", 15, 0);
        dataExists = false;
    }

    public void populate(String s) {
        f_nickname_ok.setString(s);
        System.out.println(s);
        options.append(f_nickname_ok);
        alert = new ChoiceGroup("New Message Alert", 2);
        alert.append("Sound Alert", null);
        alert.append("Vibrate", null);
        if (alertSound.equals("true")) {
            alert.setSelectedIndex(0, true);
        }
        if (vibrate.equals("true")) {
            alert.setSelectedIndex(1, true);
        }
        options.append(alert);
        refreshRate = new ChoiceGroup("Refresh Rate", 1);
        refreshRate.append("Manual refresh", null);
        refreshRate.append("30 s", null);
        refreshRate.append("1 min", null);
        refreshRate.append("3 min", null);
        refreshRate.setSelectedIndex(refreshIndex, true);
        options.append(refreshRate);
        options.addCommand(ok);
        options.addCommand(back);
        options.setCommandListener(this);
    }

    public void setActive() {
        Main.getDisplay().setCurrent(options);
    }

    public void simulateChatPressed() {
        if (refreshIndex == 0) {
            Main.getDiscover().cancel();
            Main.getDiscover().go();
        }
        try {
            timerRefresh.cancel();
        } catch (Exception exception) {
        }
        if (refreshIndex != 0) {
            timerRefresh = new Timer();
            timerRefresh.schedule(new TimerRefresh(false), 100L, refresh[refreshIndex]);
        }
        Main.getChooseDeviceForm().formaVisible();
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == ok && !f_nickname_ok.getString().trim().equals("")) {
            lastNameOfServer = nickname;
            oAcceptSave(f_nickname_ok);
            if (!nickname.equals(lastNameOfServer)) {
                mustRestart = new Alert("Attention", "You must restart IBM! IM for the new nickname to be seen by your friends", null, AlertType.CONFIRMATION);
                mustRestart.setTimeout(-2);
                Main.getDisplay().setCurrent(mustRestart, Main.getChooseDeviceForm().getForma());
            } else {
                simulateChatPressed();
            }
        } else if (command == back) {
            Main.getDisplay().setCurrent(Main.getChooseDeviceForm().getForma());
        }
    }

    public void oAcceptSave(TextField textfield) {
        try {
            RecordStore recordstore = RecordStore.openRecordStore("mobilemessenger_db", true);
            nickname = textfield.getString();
            byte abyte0[] = nickname.getBytes();
            try {
                alertSound = "" + alert.isSelected(0);
            } catch (NullPointerException nullpointerexception) {
            }
            byte abyte1[] = alertSound.getBytes();
            try {
                vibrate = "" + alert.isSelected(1);
            } catch (NullPointerException nullpointerexception1) {
            }
            byte abyte2[] = vibrate.getBytes();
            try {
                refreshIndex = refreshRate.getSelectedIndex();
            } catch (NullPointerException nullpointerexception2) {
            }
            byte abyte3[] = ("" + refreshIndex).getBytes();
            if (recordstore.getNumRecords() == 0) {
                recordstore.addRecord(abyte0, 0, abyte0.length);
                recordstore.addRecord(abyte1, 0, abyte1.length);
                recordstore.addRecord(abyte2, 0, abyte2.length);
                recordstore.addRecord(abyte3, 0, abyte3.length);
            } else {
                recordstore.setRecord(1, abyte0, 0, abyte0.length);
                recordstore.setRecord(2, abyte1, 0, abyte1.length);
                recordstore.setRecord(3, abyte2, 0, abyte2.length);
                recordstore.setRecord(4, abyte3, 0, abyte3.length);
            }
            recordstore.closeRecordStore();
        } catch (RecordStoreException recordstoreexception) {
            recordstoreexception.printStackTrace();
        }
    }

    public Timer getTimer() {
        return timerRefresh;
    }

    public void setTimer(boolean flag) {
        TimerRefresh timerrefresh = new TimerRefresh(flag);
        timerRefresh = new Timer();
        timerRefresh.schedule(timerrefresh, 100L, refresh[refreshIndex]);
    }
    
}