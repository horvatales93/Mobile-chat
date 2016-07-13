using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Text;

namespace MobileChatService
{
    // NOTE: You can use the "Rename" command on the "Refactor" menu to change the interface name "IService1" in both code and config file together.
    [ServiceContract]
    public interface IService1
    {

        [OperationContract]
        [WebGet(UriTemplate = "Login", ResponseFormat = WebMessageFormat.Json, RequestFormat = WebMessageFormat.Json)]
        ResponseObject Prijava();

        [OperationContract]
        [WebInvoke(Method = "POST", UriTemplate = "Register", ResponseFormat = WebMessageFormat.Json, RequestFormat = WebMessageFormat.Json)]
        ResponseObject Register();

        [OperationContract]
        [WebGet(UriTemplate = "GetAllMsges", ResponseFormat = WebMessageFormat.Json)]
        ResponseObject GetAllMessages();

        [OperationContract]
        [WebGet(UriTemplate = "GetMsges/{index}", ResponseFormat = WebMessageFormat.Json)]
        ResponseObject GetMessages(string index);

        [OperationContract]
        [WebInvoke(Method = "POST", UriTemplate = "SendMsg", ResponseFormat = WebMessageFormat.Json, RequestFormat = WebMessageFormat.Json)]
        ResponseObject SendMessage();
    }


    // Use a data contract as illustrated in the sample below to add composite types to service operations.
    [DataContract]
    public class Message
    {
        int id;
        string username;
        string message;
        DateTime time;

        [DataMember]
        public int Id
        {
            get { return id; }
            set { id = value; }
        }

        [DataMember]
        public string Username
        {
            get { return username; }
            set { username = value; }
        }

        [DataMember]
        public string Msg
        {
            get { return message; }
            set { message = value; }
        }

        [DataMember]
        public DateTime Time
        {
            get { return time; }
            set { time = value; }
        }

    }

    [DataContract]
    public class User
    {
        private string username;
        private string name;
        private string surname;
        private string password;

        [DataMember]
        public string Username
        {
            get { return username; }
            set { username = value; }
        }

        [DataMember]
        public string Name
        {
            get { return name; }
            set { name = value; }
        }

        [DataMember]
        public string Surname
        {
            get { return surname; }
            set { surname = value; }
        }

        [DataMember]
        public string Password
        {
            get { return password; }
            set { password = value; }
        }
    }
    [DataContract]
    public class ResponseObject
    {
        bool success = true;
        string error = "";
        string reason = "";
        List<Message> messages = null;

        [DataMember]
        public bool Success
        {
            get { return success; }
            set { success = value; }
        }

        [DataMember]
        public string Error
        {
            get { return error; }
            set { error = value; }
        }
        
        [DataMember]
        public string Reason
        {
            get { return reason; }
            set { reason = value; }
        }
        
        [DataMember]
        public List<Message> Messages
        {
            get { return messages; }
            set { messages = value; }
        }

    }
}
