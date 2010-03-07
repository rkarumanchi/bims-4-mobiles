package main;

//import javax.microedition.lcdui.*;
import java.io.IOException;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Ticker;
import org.netbeans.microedition.lcdui.SplashScreen;

public class showForm extends Thread{

    private SplashScreen sp;
    private Display display;
    private static Ticker ticker=new Ticker("Developed BY Team-IBM(BrOkEN@!)");
    private static Image image;

    public showForm() {
        super();
        display=Main.getDisplay();
        sp=new SplashScreen(display);
        sp.setTicker(ticker);
        try {
            image = Image.createImage("/ibm.png");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        sp.setImage(image);
        sp.setTimeout(5000);
        Main.getDisplay().setCurrent(sp);
    }


}