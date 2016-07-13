<%@ Page Language="C#" AutoEventWireup="true" CodeBehind="admin.aspx.cs" Inherits="MobileChatService.admin" %>

<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
    <title>MobileChat - Backend</title>
    <link href="style.css" rel="stylesheet" type="text/css" />
</head>
<body>
    <form id="form1" runat="server">
    <div id="adminContainer">
        
        <asp:Label CssClass="adminTitle" ID="LB_AdminTitle" runat="server" Text="Label"></asp:Label>
        
        <div id="adminContent">

            <asp:ListBox CssClass="userList" ID="ListBox1" runat="server" DataSourceID="SqlDataSource1" DataTextField="username" DataValueField="username" AutoPostBack="True" OnSelectedIndexChanged="UserSelectionChanged">
                <asp:ListItem>FirstItem</asp:ListItem>
            </asp:ListBox>

            <div id="userDetails">

                
                <asp:Label CssClass="DetailsUsername" ID="LB_DetailsUserName" runat="server" Text="Username : "></asp:Label>
                <asp:Label CssClass="DetailsName" ID="LB_DetailsName" runat="server" Text="Name : "></asp:Label>
                <asp:Label CssClass="DetailsSurname" ID="LB_DetailsSurname" runat="server" Text="Surname : "></asp:Label>
                <asp:Label CssClass="DetailsMessagesNum" ID="LB_DetailsMessageNum" runat="server" Text="Sent messages : "></asp:Label>
                <asp:Label CssClass="DetailsLastActive" ID="LB_DetailsLastActive" runat="server" Text="Last activity : "></asp:Label>
                <asp:CheckBox CssClass="DetailsAdmin" ID="CB_Admin" runat="server" Text="Admin" OnCheckedChanged="CB_Admin_CheckedChanged" AutoPostBack="True" />
                <asp:Button CssClass="BTNDetailDelete" ID="B_DetailsDelete" runat="server" Text="Delete" OnClick="B_DetailsDelete_Click" />

            </div>

            <asp:Button CssClass="BTNLogout" ID="B_Logout" runat="server" Text="Logout" OnClick="B_Logout_Click" />
            <asp:Button CssClass="BTNRefresh" ID="B_Refresh" runat="server" Text="Refresh" OnClick="B_Refresh_Click" />

        </div>
        <asp:SqlDataSource ID="SqlDataSource1" runat="server" ConnectionString="<%$ ConnectionStrings:MobileChatDBConnectionString %>" SelectCommand="SELECT * FROM [Uporabnik]"></asp:SqlDataSource>
    </div>
    </form>
</body>
</html>
