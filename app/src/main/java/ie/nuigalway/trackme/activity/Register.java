/**
 * Copyright (c) 2017 NUI Galway
 * Author: Matthew Finn
 */

package ie.nuigalway.trackme.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import ie.nuigalway.trackme.R;
import ie.nuigalway.trackme.activity.Login;

public class Register extends AppCompatActivity {
    public final static String REG_MESSAGE = "ie.nuigalway.trackme.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    //This method is called when the Register button is clicked
    public void registerUser(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public void createLocalDB(){

    }

    public void pushToGlobalDB(){

    }

    public void createSharedPreferences(){

    }
}
