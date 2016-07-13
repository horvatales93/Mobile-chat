<%@ Page Language="C#" AutoEventWireup="true" CodeBehind="adminLogin.aspx.cs" Inherits="MobileChatService.adminLogin" %>

<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
    <title>MobileChat - Backend</title>
    <link href="style.css" rel="stylesheet" type="text/css" />
</head>
<body>
    <form id="form1" runat="server">
    <div id="loginDiv">
        <span id="loginTitle">
            MobileChat - Admin Login
        </span>
        <div id="loginForm">
            <div id="innerLoginForm">
                <span class="TBLabel">Username</span>
                <asp:TextBox CssClass="LoginTB" ID="TB_Username" runat="server"></asp:TextBox>
            
                <br />
            
                <span class="TBLabel">Password</span>
                <asp:TextBox CssClass="LoginTB" ID="TB_Password" runat="server" TextMode="Password"></asp:TextBox>
                <asp:Button CssClass="LoginButton" ID="B_Login" runat="server" Text="Login" OnClick="B_Login_Click" />
            </div>
            <div id="LoginStatus">
                <asp:Label ID="LB_LoginStatus" runat="server" Text="Label">Enter admin username and password</asp:Label>
            </div>
        </div>
    </div>
    </form>
</body>
</html>
