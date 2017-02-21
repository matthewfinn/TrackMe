package ie.nuigalway.trackme.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ie.nuigalway.trackme.R;
import ie.nuigalway.trackme.helper.SessionManager;

public class Menu extends AppCompatActivity {


    /**
     *
     *
     * TBDELETED AND MOVE FN TO MENUNAV
     *
     */

    private SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    /**
     *
     *
     */
    public void onClick( ){

        Intent intent = new Intent(this, Register.class);
        startActivity(intent);

    }

    public void logOut(){

        sm.logOutUser();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}
