using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace MobileChatService
{
    public partial class admin : System.Web.UI.Page
    {
        string connString = "Data Source=tcp:fri-is-db-9381.database.windows.net,1433;Initial Catalog=MobileChatDB;User ID=nejc@fri-is-db-9381;Password=RazmisljaSeMi.2016";

        protected void Page_Load(object sender, EventArgs e)
        {
            if (Session["admin"] == null)
            {
                Response.Redirect("adminLogin.aspx");
            }
            LB_AdminTitle.Text = "Welcome " + Session["admin"].ToString();
        }

        protected void B_Logout_Click(object sender, EventArgs e)
        {
            Session["admin"] = null;
            Response.Redirect("adminLogin.aspx");
        }

        protected void B_Refresh_Click(object sender, EventArgs e)
        {
            ListBox1.DataBind();
            int tmp = ListBox1.SelectedIndex;
            ListBox1.SelectedIndex = -1;
            ListBox1.SelectedIndex = tmp;
        }

        protected void B_DetailsDelete_Click(object sender, EventArgs e)
        {
            if(ListBox1.SelectedIndex != -1)
            {
                string selectedUser = ListBox1.SelectedValue;
                SqlConnection conn = new SqlConnection();
                conn.ConnectionString = connString;

                SqlCommand cmd = new SqlCommand("DELETE FROM Pogovor WHERE username=@user; DELETE FROM Uporabnik WHERE username=@user", conn);
                cmd.Parameters.AddWithValue("@user", selectedUser);

                conn.Open();
                int rowsAffected = cmd.ExecuteNonQuery();
                conn.Close();
                if (rowsAffected > 0)
                {
                    ListBox1.Items.Remove(selectedUser);
                    ClearDetailsSection();
                }
            }
        }

        protected void CB_Admin_CheckedChanged(object sender, EventArgs e)
        {
            if(ListBox1.SelectedIndex != -1)
            {
                string selectedUser = ListBox1.SelectedValue;
                SqlConnection conn = new SqlConnection();
                conn.ConnectionString = connString;

                SqlCommand cmd = new SqlCommand("UPDATE Uporabnik SET admin=@adm WHERE username=@user", conn);
                cmd.Parameters.AddWithValue("@adm", CB_Admin.Checked);
                cmd.Parameters.AddWithValue("@user", selectedUser);

                conn.Open();
                int rowsAffected = cmd.ExecuteNonQuery();
                conn.Close();
            }
        }

        protected void UserSelectionChanged(object sender, EventArgs e)
        {
            ClearDetailsSection();
            string selectedUser = ListBox1.SelectedValue;
            SqlConnection conn = new SqlConnection();
            conn.ConnectionString = connString;

            SqlCommand cmd = new SqlCommand("SELECT username,ime,priimek,admin FROM Uporabnik WHERE username=@user; SELECT count(*) AS st FROM Uporabnik, Pogovor WHERE Uporabnik.username = Pogovor.username AND Uporabnik.username = @user; SELECT TOP(1) time FROM Pogovor WHERE username = @user ORDER BY time DESC; ", conn);
            cmd.Parameters.AddWithValue("@user", selectedUser);

            conn.Open();
            SqlDataReader reader = cmd.ExecuteReader();

            int counter = 0;

            while (reader.HasRows)
            {
                while (reader.Read())
                {
                    if (counter == 0)
                    {
                        LB_DetailsUserName.Text = "Username : " + reader.GetString(reader.GetOrdinal("username")) ;
                        LB_DetailsName.Text = "Name : " + reader.GetString(reader.GetOrdinal("ime"));
                        LB_DetailsSurname.Text = "Surname : " + reader.GetString(reader.GetOrdinal("priimek"));
                        CB_Admin.Checked = reader.GetBoolean(reader.GetOrdinal("admin"));
                    }else if(counter == 1)
                    {
                        LB_DetailsMessageNum.Text = "Sent messages : " + reader.GetInt32(reader.GetOrdinal("st")).ToString();
                    }else if(counter == 2)
                    {
                        LB_DetailsLastActive.Text = "Last activity : " + reader.GetDateTime(reader.GetOrdinal("time")).ToString();
                    }
                }
                reader.NextResult();
                counter++;
            }
            conn.Close();
        }
        private void ClearDetailsSection()
        {
            LB_DetailsUserName.Text = "Username : ";
            LB_DetailsName.Text = "Name : ";
            LB_DetailsSurname.Text = "Surname : ";
            CB_Admin.Checked = false;
            LB_DetailsMessageNum.Text = "Sent messages : ";
            LB_DetailsLastActive.Text = "Last activity : ";
        }
    }
}