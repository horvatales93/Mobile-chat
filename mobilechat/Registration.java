package ales.mobilechat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Registration extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Button btnReg = (Button) findViewById(R.id.btnRegstr);
        final EditText usr = (EditText) findViewById(R.id.etRegUsername);
        final EditText nm = (EditText) findViewById(R.id.etRegName);
        final EditText srnm = (EditText) findViewById(R.id.etRegSurname);
        final EditText pswd = (EditText) findViewById(R.id.etRegPassword);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] params = {usr.getText().toString(),nm.getText().toString(),srnm.getText().toString(),pswd.getText().toString()};
                MobileChatRegistration registr = new MobileChatRegistration();
                registr.execute(params);
            }
        });
    }

    private class MobileChatRegistration extends AsyncTask<String,Void,JSONObject>{
        String URLRegister = "http://fri-is-63120042.azurewebsites.net/Service1.svc/Register";
        ChatUser user = new ChatUser();
        String Username = "";
        String Password = "";
        @Override
        protected JSONObject doInBackground(String[] params){
            Username = params[0];
            user.Name = params[1];
            user.Surname = params[2];
            Password = params[3];
            HttpClient httpClient = new DefaultHttpClient();
            JSONObject responseData = new JSONObject();
            String result = "";

            try{
                HttpPost httpPost = new HttpPost(URLRegister);
                httpPost.setEntity(new StringEntity(user.toString(), "UTF-8"));

                httpPost.setHeader("Accept","application/json");
                String base64EncodedCredentials = "Basic " + Base64.encodeToString((Username
                        + ":" + Password).getBytes(),Base64.NO_WRAP);
                httpPost.setHeader("Authorization",base64EncodedCredentials);

                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity httpEntity = response.getEntity();
                result = EntityUtils.toString(httpEntity);
                try {
                    responseData = new JSONObject(result);
                } catch (JSONException e) {
                    new AlertDialog.Builder(Registration.this)
                            .setTitle("Deserialization")
                            .setMessage("There were problems deserializing response")
                            .setCancelable(true)
                            .show();
                }
            }catch (IOException e){
                new AlertDialog.Builder(Registration.this)
                        .setTitle("Check Connection")
                        .setMessage("Data unreachable. Check Internet connection")
                        .setCancelable(true)
                        .show();
            }

            return responseData;
        }

        @Override
        protected void onPostExecute(JSONObject responseData){
            ChatResponseObject chatResponse = new ChatResponseObject();
            try {
                chatResponse.Success = responseData.getBoolean("Success");
                chatResponse.Error = responseData.getString("Error");
                chatResponse.Reason = responseData.getString("Reason");
            } catch (JSONException e) {
                new AlertDialog.Builder(Registration.this)
                        .setTitle("Deserialization num. 2")
                        .setMessage("There were problems when deserializing to ChatResponseObject")
                        .setCancelable(true)
                        .show();
            }
            if(chatResponse.Success){
                new AlertDialog.Builder(Registration.this)
                        .setTitle("Success")
                        .setMessage("You have successfully registered")
                        .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int id){
                                dialog.dismiss();
                                Intent mainActivity = new Intent(Registration.this,MainActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("Username",Username);
                                bundle.putString("Password",Password);
                                mainActivity.putExtras(bundle);
                                startActivity(mainActivity);
                            }
                        })
                        .show();
            }else{
                new AlertDialog.Builder(Registration.this)
                        .setTitle(chatResponse.Error)
                        .setMessage(chatResponse.Reason)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int id){
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        }
    }

}
