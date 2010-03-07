package ui;

import bluetooth.Server;
import javax.microedition.lcdui.*;
import main.Main;


public class WriteMessageForm implements CommandListener{

    private TextBox textToSend;
    private static final Command send = new Command("Send", 4, 0);
    private static final Command back = new Command("Back", 2, 0);
    private ChatForm parent;
    private String friend;
    
    public WriteMessageForm(ChatForm chatform)
    {
        textToSend = new TextBox("Type message", "", 120, 0);
        textToSend.addCommand(back);
        textToSend.addCommand(send);
        textToSend.setCommandListener(this);
        parent = chatform;
    }

    public void commandAction(Command command, Displayable displayable)
    {
        System.out.println("text to send_" + textToSend);
        if(command == send && textToSend.getString() != null && !textToSend.getString().trim().equals(""))
            try
            {
                Main.getClient(null, friend, false).talk(textToSend.getString());
                parent.addMsg(Server.serverName.substring(0, Server.serverName.indexOf("_")) + ": " + textToSend.getString(), Server.serverName);
                parent.setCurrent(null, null);
            }
            catch(NullPointerException nullpointerexception)
            {
                nullpointerexception.printStackTrace();
            }
        else
        if(command == back)
            parent.setCurrent(null, null);
    }

    public void setCurrent()
    {
        Main.getDisplay().setCurrent(textToSend);
        textToSend.setString("");
        textToSend.setTicker(null);
    }

    public void setFriend(String s)
    {
        friend = s;
    }

    

}