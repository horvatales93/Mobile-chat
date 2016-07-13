package ales.mobilechat;

/**
 * Created by horva on 11. 01. 2016.
 */
public class ChatUser {
    public String Name = "";
    public String Surname = "";

    @Override
    public String toString() {
        return String.format("{\"Username\":\"\",\"Name\":\"%s\",\"Surname\":\"%s\",\"Password\":\"\"}",this.Name,this.Surname);
    }
}
