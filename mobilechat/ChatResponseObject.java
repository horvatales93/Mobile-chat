package ales.mobilechat;

import java.util.ArrayList;
import java.util.List;

import ales.mobilechat.ChatMessage;

/**
 * Created by horva on 10. 01. 2016.
 */
public class ChatResponseObject {
    public boolean Success;
    public String Error;
    public String Reason;
    public ArrayList<ChatMessage> Messages;
}
