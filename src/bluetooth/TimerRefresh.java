package bluetooth;

import java.util.TimerTask;
import main.Main;

public class TimerRefresh extends TimerTask {

    public boolean first;
    
    public TimerRefresh(boolean flag) {
        first = flag;
    }

    public void run() {
        if (!first) {
            Main.getDiscover().cancel();
        }
        Main.getDiscover().go();
    }
    
}