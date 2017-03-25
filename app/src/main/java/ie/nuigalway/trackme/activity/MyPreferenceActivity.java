package ie.nuigalway.trackme.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import ie.nuigalway.trackme.R;
import ie.nuigalway.trackme.helper.SessionManager;
import ie.nuigalway.trackme.services.FallDetectionService;

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

    public static class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        private static final String TAG = MyPreferenceFragment.class.getSimpleName();
        private static final String PREF = "TrackMePreferences";
        private static final String PREF_FD = "runFD";
        private SessionManager sm;



        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            sm = new SessionManager(getActivity());
            super.onCreate(savedInstanceState);
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

            Log.d(TAG,"onCreate");
            PreferenceManager pref = getPreferenceManager();
            pref.setSharedPreferencesName(PREF);

            //Do checking for profile type
            addPreferencesFromResource(R.xml.preference_default);

            //Check for FD change....Start Service
            //i.e check if sp contradicts running status

        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            if (key.equals(PREF_FD))
            {
                if(sm.getFD()){
                    startFallDetection();
                }else{
                    stopFallDetection();
                }
            }
        }
        private void startFallDetection() {
            Intent intent = new Intent(getActivity(), FallDetectionService.class);
            getActivity().startService(intent);

            Log.d(TAG, "Starting Service: " + FallDetectionService.class.getSimpleName());
            Toast.makeText(getActivity(), "Starting Fall Detection", Toast.LENGTH_SHORT)
                    .show();
        }
        private void stopFallDetection() {
            Intent intent = new Intent(getActivity(), FallDetectionService.class);
            getActivity().stopService(intent);

            Log.d(TAG, "Stopping Service: " + FallDetectionService.class.getSimpleName());
            Toast.makeText(getActivity(), "Stopping Fall Detection", Toast.LENGTH_SHORT)
                    .show();

        }
    }
}