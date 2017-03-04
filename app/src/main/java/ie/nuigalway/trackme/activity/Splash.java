package ie.nuigalway.trackme.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Handler;

import ie.nuigalway.trackme.R;
import ie.nuigalway.trackme.helper.SessionManager;

public class Splash extends AppCompatActivity {

    // Splash screen timeout, amount of time screen wants to be shown for
    private static int TIMEOUT = 2000;
    SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sm = new SessionManager(getApplicationContext());

        

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i;

                if (sm.isLoggedIn()) {
                    i = new Intent(Splash.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    i= new Intent(Splash.this, StartupActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, TIMEOUT);
    }
}

