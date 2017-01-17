/**
 * Copyright (c) 2017 NUI Galway
 * Author:       Matthew Finn
 */

package ie.nuigalway.trackme;


import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import android.app.ProgressDialog;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class Login extends AppCompatActivity {

    private static final String URL = "http://danu6.it.nuigalway.ie/trackMeServer/login.php";
    private static final String MESSAGE = "message";
    private static final String SUCCESS = "success";
    private EditText username, password;
    private ProgressDialog pd;
    Parser p = new Parser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.login_username);
        password = (EditText) findViewById(R.id.login_password);
    }

    public void attemptLogin(View view) {
        new CheckLogin().execute();
    }

    class CheckLogin extends AsyncTask<String, String, String> {
       // boolean loginFailed = false;
        String pw = password.getText().toString();
        String un = username.getText().toString();

        @Override
        protected void onPreExecute() {


            super.onPreExecute();
            pd = new ProgressDialog(Login.this);
            pd.setIndeterminate(false);
            pd.setMessage("Logging In");
            pd.setCancelable(true);
            pd.show();

        }

        @Override
        protected String doInBackground(String... args) {

            int s;

            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                System.out.println("USERNAME:" +un);
                System.out.println("PASSWORD:" + pw);
                params.add(new BasicNameValuePair("username", un));
                params.add(new BasicNameValuePair("password", pw));

                Log.d("request","starting");
                JSONObject js = p.makeRequest(URL, "POST", params);

                Log.d("Login attempt", js.toString());

                s = js.getInt(SUCCESS);

                if(s==1){

                    Log.d("Login Successful", js.toString());
                    Intent i = new Intent(Login.this,Menu.class);
                    finish();
                    startActivity(i);
                    return js.getString(MESSAGE);

                }else{
                    return js.getString(MESSAGE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String s){
            pd.dismiss();
            if (s != null){ Toast.makeText(Login.this, s, Toast.LENGTH_LONG).show(); }

        }
    }

}