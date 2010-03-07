/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import java.io.IOException;
import javax.microedition.lcdui.*;
import main.Main;

/**
 *
 * @author T[3]@M-BrOkEN@!
 */
public class About implements CommandListener {

    private Alert alert;
    private static Ticker ticker;
    private static Image image;
    private Command okay;
    private final String msg="Instant Bluetooth Messager v 1.0\n" +
            "\nTerms & Conditions :" +
            "\nWe are not Responsible for any Kind of Damage Occured during the usage of this Application." +
            "\nThis application is developed under GNU General Public License v3 (Can be distributed).\n" +
            "\nContribute :" +
            "\nApplication source can be downloaded from http:\\\\www.karumanchi.co.cc\\projectibm\\.\n" +
            "\nCredits :" +
            "\nBrOkEN@!-Author" +
            "\nSandy-Developer" +
            "\nPothuRaju-Developer";

    public About() {
        ticker = new Ticker("Developed BY Team-IBM(BrOkEN@!)");
        okay = new Command("OK", Command.OK, 0);
        try {
            image = Image.createImage("/ibm.png");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Alert getAlert() {
        if (alert == null) {
            // write pre-init user code here
            alert = new Alert("About",
                    msg, image, AlertType.INFO);
            alert.setTicker(ticker);
            alert.addCommand(okay);
            alert.setCommandListener(this);
            alert.setTimeout(Alert.FOREVER);
        // write post-init user code here
        }
        return alert;
    }

    public void setActive() {
        Main.getDisplay().setCurrent(this.getAlert());
    }

    public void commandAction(Command c, Displayable d) {
        if (c == okay) {
            Main.getDisplay().setCurrent(Main.getChooseDeviceForm().getForma());
        }
    }
}
