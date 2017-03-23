package ie.nuigalway.trackme.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

import ie.nuigalway.trackme.R;
import ie.nuigalway.trackme.helper.SessionManager;

public class MyPreferenceActivity extends PreferenceActivity
{
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }
    @Override
    public void onBackPressed() {

        Intent i = new Intent(this, MainActivity.class);
        finish();
        this.startActivity(i);
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            SessionManager sm = new SessionManager(getActivity());
            Log.d("HI",sm.getProfileType());
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
        }
    }
}