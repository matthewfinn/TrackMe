package ie.nuigalway.trackme.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import ie.nuigalway.trackme.R;
import ie.nuigalway.trackme.fragment.HomeFragment;
import ie.nuigalway.trackme.fragment.LoginFragment;
import ie.nuigalway.trackme.fragment.RegisterFragment;
import ie.nuigalway.trackme.helper.SessionManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoginFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener,RegisterFragment.OnFragmentInteractionListener{

    SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sm = new SessionManager(getApplicationContext());

        Fragment fragment = null;
        Class fragmentClass = null;

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
     *
     * THIS IS THE MENU THING ON TOP RIGHT...... SHOULD BE DELETED FROM XMLS
     *
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
            fragmentClass = LoginFragment.class;
        } else if (id == R.id.nav_profile) {
            fragmentClass = HomeFragment.class;
        } else if (id == R.id.nav_gps) {
            fragmentClass = LoginFragment.class;
        } else if (id == R.id.nav_fd) {
            fragmentClass = LoginFragment.class;
        } else if (id == R.id.nav_prefs) {
            fragmentClass = LoginFragment.class;
        } else if (id == R.id.nav_logout) {
            b=false;
            sm.logOutUser();
            Intent i= new Intent(this, StartupActivity.class);
            startActivity(i);
            finish();

        }
        if(b) {
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


}
