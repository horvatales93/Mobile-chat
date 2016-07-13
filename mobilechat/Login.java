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
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Login extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        Button btnRegister = (Button) findViewById(R.id.btnRegister);
        final EditText textUsername = (EditText) findViewById(R.id.etUsername);
        final EditText textPassword = (EditText) findViewById(R.id.etPassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] parameters = {"Login" , textUsername.getText().toString() , textPassword.getText().toString()};

                MobileChatLogin loginTask = new MobileChatLogin();
                loginTask.execute(parameters);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(Login.this,Registration.class);
                startActivity(register);
            }
        });
    }
    private class MobileChatLogin extends AsyncTask<String,Void,JSONObject> {
        String BaseURL = "http://fri-is-63120042.azurewebsites.net/Service1.svc/%s";
        String user = "";
        String passwd = "";
        @Override
        protected JSONObject doInBackground(String[] params) {
            String URLParameter = params [0];
            String requestURL = String.format(BaseURL,URLParameter);
            user = params[1];
            passwd = params[2];
            HttpClient httpClient = new DefaultHttpClient();
            JSONObject responseData = new JSONObject();
            String result = "";

            try{
                HttpGet request = new HttpGet(requestURL);
                request.setHeader("Accept", "application/json");
                String base64EncodedCredentials = "Basic " + Base64.encodeToString((user
                        + ":" + passwd).getBytes(),Base64.NO_WRAP);
                request.setHeader("Authorization", base64EncodedCredentials);

                HttpResponse response = httpClient.execute(request);
                HttpEntity httpEntity = response.getEntity();
                result = EntityUtils.toString(httpEntity);
                try {
                    responseData = new JSONObject(result);
                } catch (JSONException e) {
                    new AlertDialog.Builder(Login.this)
                            .setTitle("Deserialization")
                            .setMessage("There were problems deserializing response")
                            .setCancelable(true)
                            .show();
                }
            }catch (IOException e){
                new AlertDialog.Builder(Login.this)
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
                new AlertDialog.Builder(Login.this)
                        .setTitle("Deserialization num. 2")
                        .setMessage("There were problems when deserializing to ChatResponseObject")
                        .setCancelable(true)
                        .show();
            }
            if(chatResponse.Success){
                Intent mainActivity = new Intent(Login.this,MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Username",user);
                bundle.putString("Password",passwd);
                mainActivity.putExtras(bundle);
                startActivity(mainActivity);
            }else{
                new AlertDialog.Builder(Login.this)
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
