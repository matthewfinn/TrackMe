/**
 *
 * CONSIDERING USING THIS ACTIVITY TO HOST REGISTRATION/LOGIN FRAGMENTS SO THE APP DRAWER IS NOT SHOWN....
 *
 * WILL SEE IF BETTER WAY TO DO THIS EXISTS
 *
 */

package ie.nuigalway.trackme.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import ie.nuigalway.trackme.R;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
    }
}
