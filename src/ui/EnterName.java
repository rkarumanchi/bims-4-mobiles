package ui;

import javax.microedition.lcdui.*;
import javax.microedition.rms.*;
import main.Main;

public class EnterName implements CommandListener {

    private Form enterName;
    private Command start;
    private Command exit;
    private TextField f_nickname_start;
    private RecordStore RS;
    public static final String REC_STORE = "mobilemessenger_db";
    private boolean dataExists;

    public EnterName()
    {
        enterName = new Form("Enter your nickname");
        start = new Command("Start", 4, 0);
        exit = new Command("Exit", 7, 0);
        f_nickname_start = new TextField("Nickname", "", 15, 0);
        RS = null;
        dataExists = false;
    }

    public void populate()
    {
        f_nickname_start.setString("");
        enterName.append(f_nickname_start);
        enterName.addCommand(start);
        enterName.addCommand(exit);
        enterName.setCommandListener(this);
    }

    public void commandAction(Command command, Displayable displayable)
    {
        if(command == start && !f_nickname_start.getString().trim().equals(""))
        {
            Main.getOptionsForm().populate(f_nickname_start.getString());
            Main.getOptionsForm().oAcceptSave(f_nickname_start);
            Main.getDisplay().setCurrent(Main.getChooseDeviceForm().getForma());
            simulateChatPressed();
            Main.getServer().setServer(f_nickname_start.getString().trim());
        } else
        if(command == exit)
            Main.getInstance().exitApp();
    }

    public void debug_clearData()
    {
        try
        {
            RecordStore.deleteRecordStore("mobilemessenger_db");
        }
        catch(RecordStoreNotFoundException recordstorenotfoundexception)
        {
            recordstorenotfoundexception.printStackTrace();
        }
        catch(RecordStoreException recordstoreexception)
        {
            recordstoreexception.printStackTrace();
        }
    }

    public void oGetSavedData()
    {
        try
        {
            RS = RecordStore.openRecordStore("mobilemessenger_db", true);
            if(RS.getNumRecords() == 4)
            {
                byte abyte0[] = RS.getRecord(1);
                OptionsForm.nickname = new String(abyte0, 0, abyte0.length);
                abyte0 = RS.getRecord(2);
                OptionsForm.alertSound = new String(abyte0, 0, abyte0.length);
                abyte0 = RS.getRecord(3);
                OptionsForm.vibrate = new String(abyte0, 0, abyte0.length);
                abyte0 = RS.getRecord(4);
                OptionsForm.refreshIndex = Integer.parseInt(new String(abyte0, 0, abyte0.length));
                dataExists = true;
            }
            RS.closeRecordStore();
        }
        catch(RecordStoreException recordstoreexception)
        {
            System.out.println(recordstoreexception);
            recordstoreexception.printStackTrace();
        }
    }

    public void setActive()
    {
        Main.getDisplay().setCurrent(enterName);
    }

    public void show()
    {
        oGetSavedData();
        if(!dataExists)
        {
            populate();
            setActive();
        } else
        {
            Main.getServer().setServer(OptionsForm.nickname.trim());
            Main.getOptionsForm().populate(OptionsForm.nickname);
            simulateChatPressed();
        }
    }

    public void simulateChatPressed()
    {
        Main.getChooseDeviceForm().initForma();
        if(OptionsForm.refreshIndex == 0)
            Main.getDiscover().go();
        else
        if(OptionsForm.refreshIndex != 0)
            Main.getOptionsForm().setTimer(true);
    }

    
}