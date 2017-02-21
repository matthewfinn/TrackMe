package ie.nuigalway.trackme.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import ie.nuigalway.trackme.R;


public class Startup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

    }

    public void goToLogin(View view){

        Intent intent = new Intent(this, Login.class);
        startActivity(intent);


    }

    public void goToRegister(View view){

        Intent intent = new Intent(this, Register.class);
        startActivity(intent);

    }
}


