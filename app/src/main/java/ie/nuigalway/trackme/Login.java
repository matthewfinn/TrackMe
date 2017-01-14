/**
 * Copyright (c) 2017 NUI Galway
 * Author:       Matthew Finn
 */

package ie.nuigalway.trackme;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;


public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void checkLogin(View view){

        EditText username = (EditText)findViewById(R.id.login_username);
        EditText password = (EditText)findViewById(R.id.login_username);

        //get username and password from server and check them.
        String usernameRetrieved;
        String passwordRetrieved;


        if(username.getText().toString().equals("") && password.getText().toString().equals("")){
            //Successful login

        }else{


        }


        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
    }

}
