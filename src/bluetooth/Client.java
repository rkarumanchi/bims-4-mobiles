package bluetooth;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import main.ConnectionStruct;
import main.Main;

public class Client implements Runnable {

    private ServiceRecord service;
    private ConnectionStruct conn;
    private int state;
    private String message;
    private String friend;
    private String connectionURL;

    public Client(ServiceRecord servicerecord, String s, boolean flag) {
        state = 0;
        if (flag) {
            try {
                service = servicerecord;
                friend = s;
                connectionURL = servicerecord.getConnectionURL(2, false);
                conn = new ConnectionStruct();
                L2CAPConnection l2capconnection = (L2CAPConnection) Connector.open(connectionURL);
                conn.setConnection(l2capconnection);
                DataElement dataelement = servicerecord.getAttributeValue(256);
                String s1 = (String) dataelement.getValue();
                s1 = s1.substring(s1.indexOf("_") + 1);
                Main.getServer().setDis(s1, conn);
                Main.getServer().talk(s1);
                conn.write(Server.serverName);
            } catch (Exception ex) {
                Main.getChooseDeviceForm().getChatForm(s).addMsg("This person isn't available anymore", s);
                Main.getChooseDeviceForm().getChatForm(s).setFriendLeft();
                Main.getChooseDeviceForm().getChatForm(s).removeCommandChat();
            }
        }
    }

    public void talk(String s) {
        message = s;
        Thread thread = new Thread(this);
        thread.setPriority(2);
        thread.start();
    }

    public void run() {
        communicate();
    }

    public void communicate() {
        if (message != null && !message.trim().equals("")) {
            if (conn != null) {
                conn.write(message);
            } else {
                System.out.println("NULL");
            }
        }
    }

    public void closeInOut() {
        conn.closeINOUT();
    }

    public void setCommunicator(L2CAPConnection l2capconnection) {
        conn = new ConnectionStruct();
        conn.setConnection(l2capconnection);
    }
}