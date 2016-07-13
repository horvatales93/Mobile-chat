package ales.mobilechat;

import java.util.Date;

/**
 * Created by horva on 10. 01. 2016.
 */
public class ChatMessage {
    public int Id;
    public String Username;
    public String Time;
    public String Message;

    @Override
    public String toString() {
        return String.format("%s : %s",Username.substring(0,5),Message);
    }
}
