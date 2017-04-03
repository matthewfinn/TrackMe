package ie.nuigalway.trackme.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import java.util.List;

import ie.nuigalway.trackme.R;
import ie.nuigalway.trackme.fragment.AboutFragment;
import ie.nuigalway.trackme.fragment.HelpFragment;
import ie.nuigalway.trackme.fragment.HomeFragment;
import ie.nuigalway.trackme.fragment.ProfileFragment;
import ie.nuigalway.trackme.helper.SessionManager;
import ie.nuigalway.trackme.services.FallDetectionService;
import ie.nuigalway.trackme.services.GPSService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener,
        HelpFragment.OnFragmentInteractionListener {


    private static final String TAG = MainActivity.class.getSimpleName();
    private Fragment fragment;
    private Class fragmentClass;
    Toolbar toolbar;
    private SessionManager sm;
    private static Context ctx;
    private NotificationCompat.Builder notif;
    int nid = 001;
    private NotificationManager nm;
    private static final String TYPE_DEF = "default";
    private static final String TYPE_TEEN = "teenager";
    private static final String TYPE_ADULT = "adult";
    private static final String TYPE_ELDERLY = "elderly";

    private static final String PREF = "TrackMePreferences";
    int MODE = 0; //private preferences mode used to set preference permissions

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(br, new IntentFilter(FallDetectionService.CDT));
        Log.d(TAG, "onResume Receiver Registered");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        unregisterReceiver(br);
        Log.d(TAG, "onDestroy Receiver Unregistered");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        registerReceiver(br, new IntentFilter(FallDetectionService.CDT));
        Log.d(TAG, "onCreate Receiver Registered");
        sm = new SessionManager(getApplicationContext());
        String type = sm.getProfileType();


        //Set default profile preferences upon first instance of opening application
        if (type.equals(TYPE_DEF)) {
            PreferenceManager.setDefaultValues(this, PREF, MODE, R.xml.preference_default, false);
        } else if (type.equals(TYPE_TEEN)) {
            PreferenceManager.setDefaultValues(this, PREF, MODE, R.xml.preference_young, false);
        } else if (type.equals(TYPE_ADULT)) {
            PreferenceManager.setDefaultValues(this, PREF, MODE, R.xml.preference_adult, false);
        } else if (type.equals(TYPE_ELDERLY)) {
            PreferenceManager.setDefaultValues(this, PREF, MODE, R.xml.preference_elderly, false);
        } else if (type.equals(null)) {
            PreferenceManager.setDefaultValues(this, PREF, MODE, R.xml.preference_default, false);
        }

        Log.d(TAG,"PREFS:"+PreferenceManager.getDefaultSharedPreferences(this).getAll());


        if (sm.getFD()) {
            startFallDetection();
        }

        ctx = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        fragment = null;
        fragmentClass = HomeFragment.class;

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        boolean b = true;

        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;
        if (id == R.id.nav_tm) {
            fragmentClass = HomeFragment.class;
        } else if (id == R.id.nav_profile) {
            fragmentClass = ProfileFragment.class;
        } else if (id == R.id.nav_about) {
            fragmentClass = AboutFragment.class;
        } else if (id == R.id.nav_help) {
            fragmentClass = HelpFragment.class;
        } else if (id == R.id.nav_prefs) {
            Intent i = new Intent(this, MyPreferenceActivity.class);
            finish();
            this.startActivity(i);
            b = false;

        } else if (id == R.id.nav_logout) {
            b = false;

            if (sm.isGPSServiceRunning()) {
                Intent intent = new Intent(this, GPSService.class);
                stopService(intent);
            }
            sm.logOutUser();
            Intent i = new Intent(this, StartupActivity.class);
            finish();
            this.startActivity(i);


        }
        if (b) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "onReceive Intent Registered");

            Intent inten = new Intent(getApplicationContext(), FallDetectionService.class);
            inten.setAction("STOP");
            PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, inten, 0);
            Log.d(TAG, "onReceive Intent To Stop Service");


            if (notif == null) {

                nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                notif = (NotificationCompat.Builder) new NotificationCompat.Builder(MainActivity.this).
                        setSmallIcon(R.drawable.track_me_logo).
                        setContentTitle("TrackMe Fall Detetced").
                        setContentText("").
                        setDefaults(Notification.DEFAULT_VIBRATE).
                        setVisibility(Notification.VISIBILITY_PUBLIC).
                        setPriority(Notification.PRIORITY_MAX).
                        addAction(R.drawable.quantum_ic_stop_white_24, "Cancel", pendingIntent);

                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
                wl.acquire(30000);
                nm.notify(nid, notif.build());
                Log.d(TAG, "onReceive Notification built");
                updateGUI(intent);
            } else {
                Log.d(TAG, "onReceive Notification already showing");
                updateGUI(intent);
            }
        }
    };

    private void updateGUI(Intent intent) {
        Log.d(TAG, "onReceive updateGUI Registered");

        if (intent.getBooleanExtra("close", false)) {
            nm.cancel(nid);
        }
        if (intent.getExtras() != null) {

            long millisUntilFinished = intent.getLongExtra("countdown", 0);
            Log.d(TAG, "Receiving Time: " + millisUntilFinished);
            notif.setContentText("Timer: " + String.valueOf(millisUntilFinished) + " - Before SMS is Sent");
            nm.notify(nid, notif.build());

            if (millisUntilFinished == 1) {
                nm.cancel(nid);
            }
        }
    }

    private void startFallDetection() {

        Intent intent = new Intent(this, FallDetectionService.class);
        startService(intent);
        Log.d(TAG, "Starting Service: " + FallDetectionService.class.getSimpleName());
    }
}


