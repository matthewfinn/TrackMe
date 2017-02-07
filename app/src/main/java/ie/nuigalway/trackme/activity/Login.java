/**
 * Copyright (c) 2017 NUI Galway
 * Author:       Matthew Finn
 */

package ie.nuigalway.trackme.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ie.nuigalway.trackme.R;
import ie.nuigalway.trackme.application.App;
import ie.nuigalway.trackme.application.AppConfig;
import ie.nuigalway.trackme.helper.LocalDBHandler;
import ie.nuigalway.trackme.helper.SessionManager;

public class Login extends AppCompatActivity {

    private static final String TAG = Login.class.getSimpleName();
   // private Button bLogin, bRegLink;

    private EditText email, password;
    private SessionManager sm;
    private LocalDBHandler db;
    private ProgressDialog pd;
    private String em, pw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = (EditText) findViewById(R.id.login_email);
        password = (EditText) findViewById(R.id.login_password);
//        bLogin = (Button) findViewById(R.id.login_button);
//        bRegLink = (Button) findViewById(R.id.login_reglink);

        pd = new ProgressDialog(this);
        pd.setCancelable(false);


        db = new LocalDBHandler(getApplicationContext());
        sm = new SessionManager(getApplicationContext());

        if (sm.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(Login.this, Menu.class);
            startActivity(intent);
            finish();
        }


    }

    public void attemptLogin(View view) {

        em = email.getText().toString().trim();
        pw = password.getText().toString().trim();


        // Check for empty data in the form
        if (!em.isEmpty() && !pw.isEmpty()) {
            // login user
            verifyLogin(em, pw);
        } else {

            Toast.makeText(getApplicationContext(),
                    "Incorrect Email/Password Combination", Toast.LENGTH_LONG)
                    .show();
        }
    }


    public void attemptRedirectToRegister(View view) {

        Intent i = new Intent(this,Register.class);
        startActivity(i);
        finish();

    }

    private void verifyLogin(final String email, final String password){

        String req = "req_login";

        pd.setMessage("Logging In");

        if(!pd.isShowing()){
            pd.show();
        }

        StringRequest r = new StringRequest(Request.Method.POST, AppConfig.LOGIN_URL, new Response.Listener<String>() {


            @Override
            public void onResponse(String res) {

                Log.d(TAG, "Response: " + res.toString());
                if (pd.isShowing()) {
                    pd.dismiss();
                }

                try {

                    JSONObject j = new JSONObject(res);
                    boolean error = j.getBoolean("error");

                    if (!error) {
                        sm.setLogin(true);

                        String uid = j.getString("uid");
                        JSONObject user = j.getJSONObject("user");

                        String fn = user.getString("first_name");
                        String sn = user.getString("surname");
                        String em = user.getString("email");
                        String ph = user.getString("phone_no");
                        String id = user.getString("unique_id");
                        String cr = user.getString("created_at");

                        db.addUser(fn, sn, em, ph, id, cr);

                        Intent in = new Intent(Login.this, Menu.class);
                        startActivity(in);
                        finish();

                        System.out.println("HAHAHA");


                    } else {
                        String err = j.getString("error_msg");
                        Toast.makeText(getApplicationContext(), "On Response: "+err, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException jse) {

                    jse.printStackTrace();
                    Toast.makeText(getApplicationContext(), "On Response Error" + jse.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError e){

                    Log.e(TAG,"Login : "+ e.getMessage());
                    Toast.makeText(getApplicationContext(),"On Error Response: "+ e.getMessage(), Toast.LENGTH_LONG).show();

                    if(pd.isShowing()){

                        pd.dismiss();
                    }


                }

            }){

            @Override
            protected Map<String, String> getParams(){

                Map<String,String> p = new HashMap<String,String>();
                p.put("email", email);
                p.put("password",password);


                return p;
            }
        };

        App.getInstance().addToRQ(r, req);

        }
}