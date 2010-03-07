package bluetooth;

import java.util.*;
import javax.bluetooth.*;
import main.Main;
import ui.ChooseDevice;

public class Discovery extends Thread implements DiscoveryListener {

    private LocalDevice local;
    private DiscoveryAgent agent;
    private static Vector devices;
    private static Hashtable serviceTable;
    private static String serviceFound;
    private static ServiceRecord serviceRecFound;
    public static final String RFCOMM_UUID = "86b4d249fb8844d6a756ec265dd1f6a3";
    private boolean inquiry;
    private int searchService;
    private boolean mIBM;
    private boolean canceled;
    private int index;
    private Waiter waiter;
    private static Vector v = new Vector();

    public Discovery() {
        inquiry = false;
        searchService = 0;
        canceled = true;
        index = 0;
        waiter = new Waiter();
        mIBM = true;
        waiter.setPriority(2);
        waiter.start();
    }

    public synchronized void run() {
        do {
            if (!mIBM) {
                break;
            }
            try {
                wait();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            if (mIBM) {
                initDiscovery();
                doDiscoveryDevices();
            }
        } while (true);
    }

    public synchronized void go() {
        while (!canceled);
        notify();
    }

    public void cancel() {
        endDiscovery();
    }

    public synchronized void stop() {
        while (!canceled) {
            mIBM = false;
            waiter.stop();
            notify();
        }
    }

    public void initDiscovery() {
        Main.getChooseDeviceForm().rearangeElements(ChooseDevice.tick);
        serviceTable = new Hashtable();
        devices = new Vector();
        canceled = false;
        serviceFound = null;
        serviceRecFound = null;
    }

    public void deviceDiscovered(RemoteDevice remotedevice, DeviceClass deviceclass) {
        devices.addElement(remotedevice);
    }

    public void inquiryCompleted(int i) {
        inquiry = false;
        switch (i) {
            default:
                break;

            case 0: // '\0'
                synchronized (devices) {
                    if (devices.size() > 0) {
                        index = 0;
                        RemoteDevice remotedevice = (RemoteDevice) devices.elementAt(0);
                        System.out.println("un remote device - inquiry completed   " + devices.size());
                        waiter.go(remotedevice);
                    } else {
                        Main.getChooseDeviceForm().populateAndSetActive();
                        index = 0;
                        canceled = true;
                    }
                }
                break;

            case 7: // '\007'
                canceled = true;
                break;

            case 5: // '\005'
                canceled = true;
                break;
        }
    }

    public void serviceSearchCompleted(int i, int j) {
        searchService = 0;
        synchronized (devices) {
            if (devices != null && index < devices.size() - 1) {
                if (serviceFound != null && serviceRecFound != null) {
                    Main.getChooseDeviceForm().newDevice(serviceFound, serviceRecFound);
                }
                index++;
                RemoteDevice remotedevice = (RemoteDevice) devices.elementAt(index);
                waiter.go(remotedevice);
            } else {
                if (serviceFound != null && serviceRecFound != null) {
                    Main.getChooseDeviceForm().newDevice(serviceFound, serviceRecFound);
                }
                Main.getChooseDeviceForm().populateAndSetActive();
                index = 0;
                canceled = true;
            }
        }
        switch (j) {
            case 1: // '\001'
                System.out.println("everything is completed");
                break;

            case 6: // '\006'
                System.out.println("not reachable");
                break;

            case 3: // '\003'
                System.out.println("search err");
                break;

            case 4: // '\004'
                System.out.println("no records");
                break;

            case 2: // '\002'
                System.out.println("search canceled");
                break;
        }
    }

    public void servicesDiscovered(int i, ServiceRecord aservicerecord[]) {
        System.out.println("services discovered");
        boolean flag = false;
        if (aservicerecord != null) {
            for (int j = 0; j < aservicerecord.length; j++) {
                if (aservicerecord[j] == null) {
                    continue;
                }
                DataElement dataelement = aservicerecord[j].getAttributeValue(256);
                String s = (String) dataelement.getValue();
                int k = s.indexOf(Server.separatorCharacter);
                if (s != null && k != -1 && s.substring(0, k).equals(Server.serviceName)) {
                    System.out.println("put aici :D ");
                    serviceTable.put(s.substring(k + 1), aservicerecord[j]);
                    serviceFound = s.substring(k + 1);
                    serviceRecFound = aservicerecord[j];
                    flag = true;
                }
            }

        }
        if (!flag) {
            serviceFound = null;
            serviceRecFound = null;
        }
    }

    private void doDiscoveryDevices() {
        try {
            local = LocalDevice.getLocalDevice();
        } catch (BluetoothStateException bluetoothstateexception) {
            bluetoothstateexception.printStackTrace();
        }
        agent = local.getDiscoveryAgent();
        try {
            inquiry = agent.startInquiry(0x9e8b33, this);
        } catch (BluetoothStateException ex) {
        }
    }

    public void doServiceSearch(RemoteDevice remotedevice) {
        UUID auuid[] = new UUID[1];
        auuid[0] = new UUID("86b4d249fb8844d6a756ec265dd1f6a3", false);
        int ai[] = {
            256
        };
        try {
            searchService = agent.searchServices(ai, auuid, remotedevice, this);
        } catch (BluetoothStateException bluetoothstateexception) {
            bluetoothstateexception.printStackTrace();
        } catch (Exception ex) {
        }
    }

    public static Vector getDevices() {

        v = new Vector();
        for (Enumeration enumeration = serviceTable.keys(); enumeration.hasMoreElements(); v.addElement(enumeration.nextElement()));
        return v;
    }

    public static ServiceRecord getSelected(int i) {
        boolean flag = false;
        try {
            ServiceRecord servicerecord = (ServiceRecord) serviceTable.get(v.elementAt(i));
            return servicerecord;
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    private void endDiscovery() {
        if (inquiry) {
            try {
                inquiry = !agent.cancelInquiry(this);
                if (inquiry) {
                    System.out.println("Could not stop");
                }
            } catch (Exception exception) {
                System.out.println(exception.getMessage() + "  endDiscovery -> cancelInquiry");
                exception.printStackTrace();
            }
        }
        if (searchService != 0) {
            try {
                agent.cancelServiceSearch(searchService);
            } catch (Exception exception1) {
                System.out.println(exception1.getMessage() + "  endDiscovery -> searchService");
                exception1.printStackTrace();
            }
            searchService = 0;
        }
    }
}