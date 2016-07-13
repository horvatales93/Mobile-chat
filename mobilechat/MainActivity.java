package ales.mobilechat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {
    String Username = "";
    String Password = "";
    ArrayAdapter<String> listAdapter;
    ListView mainListView;
    ArrayList<String> mainList;
    int lastMessageID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent intent = getIntent();

        if(intent != null){
            final Bundle bundle = intent.getExtras();
            if(bundle != null){
                Username = bundle.getString("Username");
                Password = bundle.getString("Password");
                String[] params = {"GetAllMsges"};
                MobileChatGetMsges getAllMsges = new MobileChatGetMsges();
                getAllMsges.execute(params);
            }
        }else{
            Intent returnToLogin = new Intent(MainActivity.this,Login.class);
            startActivity(returnToLogin);
        }
        mainListView = (ListView) findViewById(R.id.lvMessages);
        mainList = new ArrayList<String>();
        listAdapter = new ArrayAdapter(this,R.layout.listelement,R.id.listitemMessage,mainList);
        mainListView.setAdapter(listAdapter);
        lastMessageID = 0;

        Button btnRefr = (Button) findViewById(R.id.btnRefresh);
        btnRefr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefreshMessages();
            }
        });

        final EditText editTextMsg = (EditText) findViewById(R.id.etInputMsg);
        Button btnSnd = (Button) findViewById(R.id.btnSend);
        btnSnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String URLParam = "SendMsg";
                String[] params = {URLParam,editTextMsg.getText().toString()};
                MobileChatSendMsg sendMsg = new MobileChatSendMsg();
                sendMsg.execute(params);
            }
        });
    }

    private void addMessagesToList(ArrayList<ChatMessage> listOfMesagges){
        for(ChatMessage m : listOfMesagges){
            mainList.add(m.toString());
            lastMessageID = m.Id;
        }
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void RefreshMessages(){
        String URLParam = String.format("GetMsges/%d",lastMessageID);
        String[] params = {URLParam};
        MobileChatGetMsges getMsges = new MobileChatGetMsges();
        getMsges.execute(params);
    }

    private class MobileChatSendMsg extends AsyncTask<String,Void,JSONObject> {
        @Override
        protected JSONObject doInBackground(String[] params) {
            String URLPost = String.format("http://fri-is-63120042.azurewebsites.net/Service1.svc/%s",params[0]);
            String msg = params[1];
            HttpClient httpClient = new DefaultHttpClient();
            JSONObject responseData = new JSONObject();
            String result = "";

            try {
                HttpPost httpPost = new HttpPost(URLPost);
                httpPost.setEntity(new StringEntity(msg, "UTF-8"));

                httpPost.setHeader("Accept", "application/json");
                String base64EncodedCredentials = "Basic " + Base64.encodeToString((Username
                        + ":" + Password).getBytes(), Base64.NO_WRAP);
                httpPost.setHeader("Authorization", base64EncodedCredentials);

                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity httpEntity = response.getEntity();
                result = EntityUtils.toString(httpEntity);

                try {
                    responseData = new JSONObject(result);
                } catch (JSONException e) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Deserialization")
                            .setMessage("There were problems deserializing response")
                            .setCancelable(true)
                            .show();
                }
            } catch (IOException e) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Check Connection")
                        .setMessage("Data unreachable. Check Internet connection")
                        .setCancelable(true)
                        .show();
            }
            return responseData;
        }
        @Override
        protected void onPostExecute(JSONObject responseData) {
            ChatResponseObject chatResponse = new ChatResponseObject();
            try {
                chatResponse.Success = responseData.getBoolean("Success");
                chatResponse.Error = responseData.getString("Error");
                chatResponse.Reason = responseData.getString("Reason");
                if(chatResponse.Success){
                    EditText etMsgBox = (EditText) findViewById(R.id.etInputMsg);
                    etMsgBox.setText("");
                    RefreshMessages();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

    }
    private class MobileChatGetMsges extends AsyncTask<String,Void,JSONObject>{
        String BaseURL = "http://fri-is-63120042.azurewebsites.net/Service1.svc/%s";

        @Override
        protected JSONObject doInBackground(String[] params) {
            String requestURL = String.format(BaseURL,params[0]);
            JSONObject responseData = new JSONObject();
            String result;
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet getAllMsges = new HttpGet(requestURL);

            getAllMsges.setHeader("Accept","application/json");
            String base64EncodedCredentials = "Basic " + Base64.encodeToString((Username
                    + ":" + Password).getBytes(), Base64.NO_WRAP);
            getAllMsges.setHeader("Authorization",base64EncodedCredentials);

            try {
                HttpResponse response = httpClient.execute(getAllMsges);
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity);
                try {
                    responseData = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseData;
        }

        @Override
        protected void onPostExecute(JSONObject responseData) {
            ChatResponseObject chatResponse = new ChatResponseObject();
            try {
                chatResponse.Success = responseData.getBoolean("Success");
                chatResponse.Error = responseData.getString("Error");
                chatResponse.Reason = responseData.getString("Reason");
                if(chatResponse.Success){
                    ArrayList<ChatMessage> lstOfMsgs = new ArrayList<ChatMessage>();
                    JSONArray array = responseData.getJSONArray("Messages");
                    for(int i=0;i<array.length();i++){
                        ChatMessage msg = new ChatMessage();
                        JSONObject currentMsg = array.getJSONObject(i);
                        msg.Username = currentMsg.getString("Username");
                        msg.Message = currentMsg.getString("Msg");
                        msg.Id = currentMsg.getInt("Id");
                        msg.Time = currentMsg.getString("Time");
                        lstOfMsgs.add(msg);
                    }
                    addMessagesToList(lstOfMsgs);
                }else{
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(chatResponse.Error)
                            .setMessage(chatResponse.Reason)
                            .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,int id){
                                    dialog.dismiss();
                                    Intent returnToLogin = new Intent(MainActivity.this,Login.class);
                                    startActivity(returnToLogin);
                                }
                            })
                            .show();
                }

            } catch (JSONException e) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Deserialization num. 2")
                        .setMessage("There were problems when deserializing to ChatResponseObject")
                        .setCancelable(true)
                        .show();
            }

        }
    }
}
