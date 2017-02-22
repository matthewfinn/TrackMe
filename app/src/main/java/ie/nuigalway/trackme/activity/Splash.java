package ie.nuigalway.trackme.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Handler;

import ie.nuigalway.trackme.R;

public class Splash extends AppCompatActivity {

    // Splash screen timeout, amount of time screen wants to be shown for
    private static int TIMEOUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i = new Intent(Splash.this, MainActivity.class);
                startActivity(i);

                finish();
            }
        }, TIMEOUT);
    }
}

