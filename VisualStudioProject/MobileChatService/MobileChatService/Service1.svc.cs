using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.IO;
using System.Linq;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Json;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Text;
using System.Web.Script.Serialization;

namespace MobileChatService
{
    // NOTE: You can use the "Rename" command on the "Refactor" menu to change the class name "Service1" in code, svc and config file together.
    // NOTE: In order to launch WCF Test Client for testing this service, please select Service1.svc or Service1.svc.cs at the Solution Explorer and start debugging.
    public class Service1 : IService1
    {
        static string connString = "Data Source=tcp:fri-is-db-9381.database.windows.net,1433;Initial Catalog=MobileChatDB;User ID=nejc@fri-is-db-9381;Password=RazmisljaSeMi.2016";
        static SqlConnection conn = new SqlConnection() { ConnectionString = connString };

        public ResponseObject Prijava()
        {
            return AuthenticateUser() ? new ResponseObject() { Success = true } : new ResponseObject() { Success = false , Error = "Unauthorized Access",
                Reason = "Wrong username/password" };
        }

        private bool AuthenticateUser()
        {
            string usernamePassword;
            WebOperationContext ctx = WebOperationContext.Current;
            string authHeader = ctx.IncomingRequest.Headers[System.Net.HttpRequestHeader.Authorization];

            if (authHeader == null)
            {
                return false;
            }
            if(authHeader.StartsWith("Basic"))
            {
                string encodedCredentials = authHeader.Split(' ')[1];
                Encoding encoding = Encoding.GetEncoding("iso-8859-1");
                usernamePassword = encoding.GetString(Convert.FromBase64String(encodedCredentials));
            }
            else
            {
                return false;
            }

            string[] credentials = usernamePassword.Split(':');

            if(credentials.Length == 2 && Login(credentials[0], credentials[1]))
            {
                return true;
            }
            return false;
        }

        private bool Login(string username, string password)
        {
            SqlCommand cmd = new SqlCommand("SELECT count(*) FROM Uporabnik WHERE username=@user AND geslo=@passwd", conn);
            cmd.Parameters.AddWithValue("@user", username);
            cmd.Parameters.AddWithValue("@passwd", password);

            conn.Open();
            string result = cmd.ExecuteScalar().ToString();
            conn.Close();

            if(result == "1")
            {
                return true;
            }
            return false;
        }

        public ResponseObject GetAllMessages()
        {
            List<Message> listOfMsgs = new List<Message>();

            if (!AuthenticateUser())
            {
                return new ResponseObject() { Success = false, Error = "Unauthorized Access", Reason = "Wrong username/password" };
            }

            SqlCommand cmd = new SqlCommand("SELECT * FROM Pogovor", conn);
            cmd.CommandType = System.Data.CommandType.Text;

            conn.Open();

            SqlDataReader reader = cmd.ExecuteReader();

            while (reader.HasRows)
            {
                
                while (reader.Read())
                {
                    listOfMsgs.Add(new Message() { Id = reader.GetInt32(reader.GetOrdinal("Id")),
                        Username = reader.GetString(reader.GetOrdinal("username")),
                        Msg = reader.GetString(reader.GetOrdinal("besedilo")),
                        Time = reader.GetSqlDateTime(reader.GetOrdinal("time")).Value });
                }
                reader.NextResult();
            }


            conn.Close();

            return new ResponseObject() { Success = true, Messages = listOfMsgs };
        }

        public ResponseObject GetMessages(string index)
        {
            List<Message> listOfMsgs = new List<Message>();

            if (!AuthenticateUser())
            {
                return new ResponseObject() { Success = false, Error = "Unauthorized Access", Reason = "Wrong username/password"};
            }

            SqlCommand cmd = new SqlCommand("SELECT * FROM Pogovor WHERE Id>@id", conn);
            cmd.Parameters.AddWithValue("@id", index);

            conn.Open();
            SqlDataReader reader = cmd.ExecuteReader();

            while (reader.HasRows)
            {
                while (reader.Read())
                {
                    listOfMsgs.Add(new Message() { Id = reader.GetInt32(reader.GetOrdinal("Id")),
                        Msg = reader.GetString(reader.GetOrdinal("besedilo")),
                        Username = reader.GetString(reader.GetOrdinal("username")),
                        Time = reader.GetSqlDateTime(reader.GetOrdinal("time")).Value });
                }
                reader.NextResult();
            }

            conn.Close();

            return new ResponseObject() { Success = true, Messages = listOfMsgs };
        }

        public ResponseObject Register()
        {
            string requestBody = Encoding.UTF8.GetString(OperationContext.Current.RequestContext.RequestMessage.GetBody<byte[]>());
            JavaScriptSerializer serializer = new JavaScriptSerializer();
            User newUser = serializer.Deserialize<User>(requestBody);

            string[] usernamePassword = DecryptBasicAuthHeader();
            newUser.Username = usernamePassword[0];
            newUser.Password = usernamePassword[1];

            if(newUser.Username.Length < 6)
            {
                return new ResponseObject() { Success = false, Error = "Registration not successfull", Reason = "Username length must be between 6 and 20" };
            }

            SqlCommand cmd = new SqlCommand("SELECT count(*) FROM Uporabnik WHERE username=@user", conn);
            cmd.Parameters.AddWithValue("@user", newUser.Username);

            conn.Open();
            string result = cmd.ExecuteScalar().ToString();
            conn.Close();

            if(result == "1")
            {
                return new ResponseObject() { Success = false, Error = "Registration not successfull", Reason = "Username already in use" };
            }
            else
            {
                SqlCommand cmdAddUser = new SqlCommand("INSERT INTO Uporabnik(username,ime,priimek,geslo,admin) VALUES(@user,@name,@surname,@passwd,@admin)", conn);
                cmdAddUser.Parameters.AddWithValue("@user", newUser.Username);
                cmdAddUser.Parameters.AddWithValue("@name", newUser.Name);
                cmdAddUser.Parameters.AddWithValue("@surname", newUser.Surname);
                cmdAddUser.Parameters.AddWithValue("@passwd", newUser.Password);
                cmdAddUser.Parameters.AddWithValue("@admin", 0);

                conn.Open();
                cmdAddUser.ExecuteScalar();
                conn.Close();

            }
            return new ResponseObject() { Success = true } ;
        }

        public ResponseObject SendMessage()
        {
            if (!AuthenticateUser())
            {
                return new ResponseObject() { Success = false, Error = "Unauthorized Access", Reason = "Wrong username/password" };
            }

            string username = DecryptBasicAuthHeader()[0];

            string msg = Encoding.UTF8.GetString(OperationContext.Current.RequestContext.RequestMessage.GetBody<byte[]>());
            if (msg != "")
            {

                string cmdString = "INSERT INTO Pogovor(username,besedilo,time) VALUES(@user, @besedilo, @time)";
                SqlCommand cmd = new SqlCommand(cmdString, conn);
                cmd.CommandType = System.Data.CommandType.Text;
                cmd.Parameters.AddWithValue("@user", username);
                cmd.Parameters.AddWithValue("@besedilo", msg);
                cmd.Parameters.AddWithValue("@time", DateTime.Now.ToUniversalTime().AddHours(1));

                conn.Open();
                cmd.ExecuteScalar();
                conn.Close();

            }
            return new ResponseObject { Success = true };
        }

        private string[] DecryptBasicAuthHeader()
        {
            WebOperationContext ctx = WebOperationContext.Current;
            string authHeader = ctx.IncomingRequest.Headers[System.Net.HttpRequestHeader.Authorization];

            string encodedCredentials = authHeader.Split(' ')[1];
            Encoding encoding = Encoding.GetEncoding("iso-8859-1");
            string usernamePassword = encoding.GetString(Convert.FromBase64String(encodedCredentials));
            return usernamePassword.Split(':');
        }

    }
}
