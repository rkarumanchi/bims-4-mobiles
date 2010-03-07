package bluetooth;

import java.io.IOException;
import javax.bluetooth.RemoteDevice;
import main.Main;

public class Waiter extends Thread {

    private boolean isWaiting;
    private RemoteDevice remoteDevice;
    
    public Waiter() {
        isWaiting = true;
    }

    public synchronized void run() {
        do {
            if (!isWaiting) {
                break;
            }
            try {
                wait();
            } catch (InterruptedException ex) {
            }
            if (isWaiting) {
                try {
                    System.out.println("friendly name :" + remoteDevice.getFriendlyName(true));
                } catch (IOException ioexception) {
                    ioexception.printStackTrace();
                }
                Main.getDiscover().doServiceSearch(remoteDevice);
            }
        } while (true);
    }

    public synchronized void go(RemoteDevice remotedevice) {
        remoteDevice = remotedevice;
        notify();
    }

    public synchronized void stop() {
        isWaiting = false;
        notify();
    }

}