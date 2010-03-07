package bluetooth;

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.lcdui.AlertType;
import main.ConnectionStruct;
import main.Main;
import ui.*;

public class Server implements Runnable {

    private static LocalDevice local;
    private static L2CAPConnectionNotifier noty;
    private static L2CAPConnection connection;
    private ConnectionStruct conn;
    private static Hashtable allConns;
    private static boolean finished = false;
    private String connectionURL;
    public static String serviceName = "IBM";
    public static String serverName;
    public static String separatorCharacter = "_";

    private class InnerListener
            implements Runnable {

        public void startInnerListener(String s) {
            friend = s;
            Thread thread = new Thread(this);
            thread.setPriority(2);
            thread.start();
        }

        public void run() {
            String s = "";
            boolean flag = false;
            ConnectionStruct connectionstruct = null;
            if (Server.allConns.size() != 0 && Server.allConns.containsKey(friend)) {
                connectionstruct = (ConnectionStruct) Server.allConns.get(friend);
            }
            if (connectionstruct != null && friend != null) {
                do {
                    if (flag) {
                        break;
                    }
                    String s1;
                    if (connectionstruct != null) {
                        if ((s1 = connectionstruct.read()) != null) {
                            if (OptionsForm.alertSound.equals("true")) {
                                AlertType.INFO.playSound(Main.getDisplay());
                            }
                            if (OptionsForm.vibrate.equals("true")) {
                                Main.getDisplay().vibrate(800);
                            }
                            Main.getChooseDeviceForm().getChatForm(friend).addMsg(friend.substring(0, friend.indexOf("_")) + ": " + s1.toString(), friend);
                            if (Main.getChooseDeviceForm().getChatForm(friend).isFormShown()) {
                                Main.getChooseDeviceForm().getChatForm(friend).scroll();
                            }
                        } else {
                            flag = true;
                            Main.getChooseDeviceForm().getChatForm(friend).addMsg("< " + friend.substring(0, friend.indexOf("_")) + " left >", friend);
                            Main.getChooseDeviceForm().getChatForm(friend).removeCommandChat();
                            if (Main.getChooseDeviceForm().getChatForm(friend).isFormShown()) {
                                Main.getChooseDeviceForm().getChatForm(friend).scroll();
                            }
                            Main.getChooseDeviceForm().getChatForm(friend).setFriendLeft();
                        }
                    }
                } while (true);
            }
        }
        private String friend;

        private InnerListener() {
        }
    }

    public Server() {
    }

    public void setServer(String s) {
        try {
            local = LocalDevice.getLocalDevice();
            local.setDiscoverable(0x9e8b33);
        } catch (BluetoothStateException bluetoothstateexception) {
            bluetoothstateexception.printStackTrace();
        }
        Server _tmp = this;
        serverName = s + "_" + local.getBluetoothAddress();
        connectionURL = "btl2cap://localhost:86b4d249fb8844d6a756ec265dd1f6a3;name=" + serviceName + separatorCharacter + serverName;
        startServer();
    }

    public void startServer() {
        finished = false;
        allConns = new Hashtable();
        Thread thread = new Thread(this);
        thread.setPriority(2);
        thread.start();
    }

    public void stopServer() {
        try {
            finished = true;
            if (conn != null) {
                conn.closeINOUT();
                conn = null;
            }
            for (Enumeration enumeration = allConns.elements();
                    enumeration.hasMoreElements();
                    ((ConnectionStruct) enumeration.nextElement()).closeINOUT()) {
                allConns.clear();
                allConns = null;
            }
            if (noty != null) {
                noty.close();
                noty = null;
            }
            System.gc();
        } catch (IOException ioexception) {
        } catch (Exception exception) {
        }
        Main.getInstance().exitApp();
    }

    public void run() {
        try {
            if (noty == null) {
                noty = (L2CAPConnectionNotifier) Connector.open(connectionURL);
            }
            openConnection();
        } catch (BluetoothStateException bluetoothstateexception) {
            bluetoothstateexception.printStackTrace();
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        } catch (NullPointerException nullpointerexception) {
            nullpointerexception.printStackTrace();
        } catch (ClassCastException classcastexception) {
            System.out.println("@@@ CAST");
            classcastexception.printStackTrace();
        }
    }

    public void openConnection() {
        do {
            if (finished) {
                break;
            }
            try {
//                noty = (L2CAPConnectionNotifier) Connector.open(connectionURL);
                if (noty != null) {
                    connection = noty.acceptAndOpen();
                }
                if (connection != null && noty != null) {
                    conn = new ConnectionStruct();
                    conn.setConnection(connection);
                    String s = conn.read();
                    allConns.put(s, conn);
                    Main.getChooseDeviceForm().addToDisplayable(s);
                    Main.getClient(null, s, false).setCommunicator(connection);
                    talk(s);
                }
            } catch (InterruptedIOException interruptedioexception) {
                System.out.println("Interrupted NOTY  " + interruptedioexception.getMessage());
                interruptedioexception.printStackTrace();
//                this.run();
            } catch (IOException ioexception) {
                System.out.println("CLOSE NOTY  " + ioexception.getMessage());
                ioexception.printStackTrace();
//                this.run();
            } catch (NullPointerException nullpointerexception) {
                System.out.println("NULL POINTER EX. " + nullpointerexception.getMessage());
                nullpointerexception.printStackTrace();
//                this.run();
            }
        } while (true);
    }

    public void talk(String s) {
        InnerListener innerlistener = new InnerListener();
        innerlistener.startInnerListener(s);
    }

    public String getConnectionURL() {
        return connectionURL;
    }

    public void setDis(String s, ConnectionStruct connectionstruct) {
        allConns.put(s, connectionstruct);
    }
}