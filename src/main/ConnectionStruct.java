package main;

import java.io.IOException;
import javax.bluetooth.L2CAPConnection;

public class ConnectionStruct {

    L2CAPConnection connection;
    
    public ConnectionStruct() {
        connection = null;
    }

    public void setConnection(L2CAPConnection l2capconnection) {
        connection = l2capconnection;
        System.err.println("connection established");
    }

    public void write(String s) {
        try {
            if (connection != null) {
                System.out.println("Diff de null");
            }
            if (s != null && !s.equals("")) {
                byte abyte0[] = s.getBytes();
                connection.send(abyte0);
            } else {
                System.out.println("NULL");
            }
        } catch (IOException ioexception) {
        } catch (NullPointerException nullpointerexception) {
            nullpointerexception.printStackTrace();
        }
    }

    public String read() {
        byte abyte0[];
        abyte0 = null;
        if (connection == null) {
            System.out.println("conn nu e null");
        }

        try {
            abyte0 = new byte[connection.getTransmitMTU()];
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
        byte abyte1[];
        try {
            int i = connection.receive(abyte0);
            abyte1 = new byte[i];
            System.arraycopy(abyte0, 0, abyte1, 0, i);
            return new String(abyte1);
        } catch (IOException iOException) {
            System.out.println("conn E null");
            return null;
        }


    }

    public boolean isReady() {
        try {
            if (connection != null) {
                return connection.ready();
            } else {
                return false;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void closeINOUT() {
        try {
            connection.close();
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
        connection = null;
    }

    public L2CAPConnection getConnection() {
        return connection;
    }
    
}