package ie.nuigalway.trackme.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import ie.nuigalway.trackme.R;

public class MyPreferenceActivity extends PreferenceActivity
{
    private static final String TAG = MyPreferenceActivity.class.getSimpleName();

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }
    @Override
    public void onBackPressed() {

        Log.d(TAG,"onBackPressed");
        Intent i = new Intent(this, MainActivity.class);
        finish();
        this.startActivity(i);
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        private static final String TAG = MyPreferenceFragment.class.getSimpleName();
        private static final String PREF = "TrackMePreferences";

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            Log.d(TAG,"onCreate");
            PreferenceManager pref = getPreferenceManager();
            pref.setSharedPreferencesName(PREF);

            //Do checking for profile type
            addPreferencesFromResource(R.xml.preference_default);

            //Check for FD change....Start Service
            //i.e check if sp contradicts running status

        }
    }
}