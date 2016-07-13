using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace MobileChatService
{
    public partial class adminLogin : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            if(Session["admin"] != null)
            {
                Response.Redirect("admin.aspx");
            }
        }

        protected void B_Login_Click(object sender, EventArgs e)
        {
            SqlConnection conn = new SqlConnection();
            conn.ConnectionString = "Data Source=tcp:fri-is-db-9381.database.windows.net,1433;Initial Catalog=MobileChatDB;User ID=nejc@fri-is-db-9381;Password=RazmisljaSeMi.2016";

            SqlCommand cmd = new SqlCommand("SELECT count(*) FROM Uporabnik WHERE username=@user AND geslo=@passwd AND admin=@admin",conn);
            cmd.Parameters.AddWithValue("@user", TB_Username.Text);
            cmd.Parameters.AddWithValue("@passwd", TB_Password.Text);
            cmd.Parameters.AddWithValue("@admin", true);

            conn.Open();
            string result = cmd.ExecuteScalar().ToString();
            conn.Close();

            if(result == "1")
            {
                Session["admin"] = TB_Username.Text;
                Response.Redirect("admin.aspx");
            }
            else
            {
                LB_LoginStatus.Text = "Username/Password is incorrect.";
                LB_LoginStatus.ForeColor = System.Drawing.Color.DarkRed;
            }
        }
    }
}