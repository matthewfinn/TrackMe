package ie.nuigalway.trackme.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import ie.nuigalway.trackme.R;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void onClick(View view){

        Intent intent = new Intent(this, Register.class);
        startActivity(intent);

    }

    public void logOut(View view){


        view.getContext().getSharedPreferences("TrackMePreferences", 0).edit().clear().commit();
    }
}
