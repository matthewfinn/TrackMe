package ie.nuigalway.trackme.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.annotation.NonNull;

import java.util.List;


import ie.nuigalway.trackme.R;
import ie.nuigalway.trackme.fragment.ContactsFragment;
import ie.nuigalway.trackme.fragment.FDPreferencesFragment;
import ie.nuigalway.trackme.fragment.GPSPreferencesFragment;
import ie.nuigalway.trackme.fragment.HomeFragment;
import ie.nuigalway.trackme.fragment.LoginFragment;
import ie.nuigalway.trackme.fragment.PreferencesFragment;
import ie.nuigalway.trackme.fragment.ProfileFragment;
import ie.nuigalway.trackme.fragment.RegisterFragment;
import ie.nuigalway.trackme.helper.SessionManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoginFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener,RegisterFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener, ContactsFragment.OnFragmentInteractionListener, FDPreferencesFragment.OnFragmentInteractionListener,
        PreferencesFragment.OnFragmentInteractionListener {

    SessionManager sm;

    private static final int FL_PERMISSION = 1;
    private static final int CL_PERMISSION = 0;
    private static final int NS_PERMISSION = 2;

    private Fragment fragment;
    private Class fragmentClass;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sm = new SessionManager(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragment = null;
        fragmentClass = null;

//        int aflCheck = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION);
////        int aclCheck = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_COARSE_LOCATION);
//        int anpCheck = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_NETWORK_STATE);

//        if(anpCheck != PackageManager.PERMISSION_GRANTED){
//            //CL_PERMISSION=1;
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, NS_PERMISSION);
//
//        }
//        if(aclCheck != PackageManager.PERMISSION_GRANTED){
//            //CL_PERMISSION=1;
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, CL_PERMISSION);
//
//        }
//        if (aflCheck != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    FL_PERMISSION);
//

       // }else {

            if (savedInstanceState == null) {
                fragmentClass = HomeFragment.class;
            }
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
     //   }

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


    /**
     * THIS IS THE MENU THING ON TOP RIGHT...... SHOULD BE DELETED FROM XMLS
     */
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
        } else if (id == R.id.nav_gps) {
            fragmentClass = GPSPreferencesFragment.class;
        } else if (id == R.id.nav_fd) {
            fragmentClass = FDPreferencesFragment.class;
        } else if (id == R.id.nav_prefs) {
            fragmentClass = PreferenceFragment.class;
        } else if (id == R.id.nav_logout) {
            b = false;
            sm.logOutUser();
            Intent i = new Intent(this, StartupActivity.class);
            startActivity(i);
            finish();

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


//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//
//        Fragment f;
//        Class c;
//        switch (requestCode) {
//
//            case FL_PERMISSION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//
//                    c = HomeFragment.class;
//
//                } else {
//
//                    c = LoginFragment.class;
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                try {
//                    f = (Fragment) c.newInstance();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
//
//                // other 'case' lines to check for other
//                // permissions this app might request
//            }
//        }
//
//    }

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
}


