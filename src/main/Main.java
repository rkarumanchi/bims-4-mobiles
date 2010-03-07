package main;

import bluetooth.*;
import java.util.Hashtable;
import javax.bluetooth.ServiceRecord;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import ui.*;

public class Main extends MIDlet {

    public static boolean debug = true;
    private static EnterName nickForm = new EnterName();
    private static OptionsForm options = new OptionsForm();
    private static About about = new About();
    private static Display display;
    private static Discovery discover;
    private static Server server;
    private static Hashtable client = new Hashtable();
    private static Main instance;
    private static ChooseDevice devicesForm;

    public Main() {
        instance = this;
        display = Display.getDisplay(instance);
        devicesForm = new ChooseDevice();
    }

    protected void destroyApp(boolean flag)
            throws MIDletStateChangeException {
    }

    protected void pauseApp() {
    }

    protected void startApp()
            throws MIDletStateChangeException {
        showForm sf = new showForm();
        sf.setPriority(1);
        try {
            showForm.sleep(5000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        discover = new Discovery();
        discover.setPriority(2);
        discover.start();
        nickForm.show();
    }

    public static Display getDisplay() {
        return display;
    }

    public static Discovery getDiscover() {
        if (discover == null) {
            return discover = new Discovery();
        } else {
            return discover;
        }
    }

    public static Server getServer() {
        if (server == null) {
            return server = new Server();
        } else {
            return server;
        }
    }

    public static Main getInstance() {
        return instance;
    }

    public static Client getClient(ServiceRecord servicerecord, String s, boolean flag) {
        System.out.println("numePrieten:" + s);
        if (!client.containsKey(s)) {
            Client client1 = new Client(servicerecord, s, flag);
            client.put(s, client1);
            return client1;
        } else {
            return (Client) client.get(s);
        }
    }

    public static boolean existsClient(String s) {
        return client.containsKey(s);
    }

    public static void removeClient(String s) {
        if (client.containsKey(s)) {
            client.remove(s);
        }
    }

    public static ChooseDevice getChooseDeviceForm() {
        return devicesForm;
    }

    public static EnterName getNickForm() {
        return nickForm;
    }

    public static OptionsForm getOptionsForm() {
        return options;
    }

    public static About getAboutForm() {
        return about;
    }

    public void exitApp() {
        try {
            destroyApp(false);
        } catch (MIDletStateChangeException midletstatechangeexception) {
            midletstatechangeexception.printStackTrace();
        }
        notifyDestroyed();
    }
}