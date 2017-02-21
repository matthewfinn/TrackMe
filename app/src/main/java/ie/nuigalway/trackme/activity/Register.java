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
import android.widget.Spinner;
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

public class Register extends AppCompatActivity {

    private static final String TAG = Register.class.getSimpleName();
   // private Button bRegister, bLoginLink;
    private EditText fName,surname,email, password, phno;
    private Spinner type;
    private SessionManager sesh;
    private LocalDBHandler db;
    private ProgressDialog pd;
    //private String fn, sn, em, ph, pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        fName = (EditText) findViewById(R.id.edit_fname);
        surname = (EditText) findViewById(R.id.edit_surname);
        phno = (EditText) findViewById(R.id.edit_phno);
        email = (EditText) findViewById(R.id.edit_email);
        password = (EditText) findViewById(R.id.edit_password);

//        bRegister = (Button) findViewById(R.id.reg_btn);
//        bLoginLink = (Button) findViewById(R.id.lgn_lnk);

        pd = new ProgressDialog(this);
        pd.setCancelable(false);


        db = new LocalDBHandler(getApplicationContext());
        sesh = new SessionManager(getApplicationContext());

        if (sesh.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(Register.this, MenuNav.class);
            startActivity(intent);
            finish();
        }
    }

    public void attemptRegister(View view) {

        String fn = fName.getText().toString().trim();
        String sn = surname.getText().toString().trim();
        String ph = phno.getText().toString().trim();
        String em = email.getText().toString().trim();
        String pw = password.getText().toString().trim();

        // Check for empty data in the form
        if (!fn.isEmpty() && !sn.isEmpty() && !ph.isEmpty() && !em.isEmpty() && !pw.isEmpty()) {

            registerUser(fn,sn, ph, em, pw);

        } else {

            Toast.makeText(getApplicationContext(),
                    "Not all information..", Toast.LENGTH_LONG)
                    .show();
        }
    }


    public void attemptRedirectToLogin(View view) {

        Intent i = new Intent(this,Login.class);
        startActivity(i);
        finish();

    }

    private void registerUser(final String fn, final String sn,final String ph, final String em, final String pw){

        String req = "req_register";

        pd.setMessage("Registering Information on TrackMe Server");

        if(!pd.isShowing()){
            pd.show();
        }

        StringRequest r = new StringRequest(Request.Method.POST, AppConfig.REGISTRATION_URL, new Response.Listener<String>() {


            @Override
            public void onResponse(String res) {

                Log.d(TAG, "Register Response: " + res);

                if (pd.isShowing()) {
                    pd.dismiss();
                }

                try {

                    JSONObject j = new JSONObject(res);
                    boolean error = j.getBoolean("error");
                    System.out.print(j.toString());

                    if (!error) {
                        String uid = j.getString("uid");
                        JSONObject user = j.getJSONObject("user");

                        String fn = user.getString("first_name");
                        String sn = user.getString("surname");
                        String em = user.getString("email");
                        String ph = user.getString("phone_no");
                        String cr = user.getString("created_at");

                        db.addUser(uid, fn, sn, em, ph, cr);

                        //sesh.startLoginSession(true,fn, sn, em, ph);
                        Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();


                        Intent in = new Intent(Register.this, Login.class);
                        startActivity(in);
                        finish();


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

                Log.e(TAG,"Registration Error : "+ e.getMessage());
                Toast.makeText(getApplicationContext(),"On Error Response: "+ e.getMessage(), Toast.LENGTH_LONG).show();

                if(pd.isShowing()){

                    pd.dismiss();
                }
            }

        }){

            @Override
            protected Map<String, String> getParams(){

                Map<String,String> p = new HashMap<>();
                p.put("first_name", fn);
                p.put("surname",sn);
                p.put("phone_no", ph);
                p.put("email", em);
                p.put("password", pw);

                return p;
            }
        };
        App.getInstance().addToRQ(r, req);
    }
}