package ie.nuigalway.trackme.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import ie.nuigalway.trackme.R;
import ie.nuigalway.trackme.helper.SessionManager;
import ie.nuigalway.trackme.services.FallDetectionService;
import ie.nuigalway.trackme.services.GPSService;

public class MyPreferenceActivity extends PreferenceActivity
{
    private static final String TAG = MyPreferenceActivity.class.getSimpleName();
    final static int CTC = 1;


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
        private static final String PREF_BOUND = "boundary"; // Boundary Distance Preference
        private static final String PREF_CT = "soscontact"; // Boundary Distance Preference

        private static final String TYPE_DEF = "default";
        private static final String TYPE_TEEN = "teenager";
        private static final String TYPE_ADULT = "adult";
        private static final String TYPE_ELDERLY = "elderly";
        private SessionManager sm;
        String type;



        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            sm = new SessionManager(getActivity());
            type = sm.getProfileType();
            super.onCreate(savedInstanceState);
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

            Log.d(TAG,"onCreate");
            PreferenceManager pref = getPreferenceManager();
            pref.setSharedPreferencesName(PREF);
            //Do checking for profile type and load preferences base on profile type
            if (type.equals(TYPE_DEF)){

                addPreferencesFromResource(R.xml.preference_default);
                Log.d(TAG, getPreferenceManager().getSharedPreferences().getAll().toString());

            }else if(type.equals(TYPE_TEEN)){

                addPreferencesFromResource(R.xml.preference_young);
                Log.d(TAG, getPreferenceManager().getSharedPreferences().getAll().toString());

            }else if(type.equals(TYPE_ADULT)){

                addPreferencesFromResource(R.xml.preference_adult);
                Log.d(TAG, getPreferenceManager().getSharedPreferences().getAll().toString());

            }else if(type.equals(TYPE_ELDERLY)) {

                addPreferencesFromResource(R.xml.preference_elderly);
                Log.d(TAG, getPreferenceManager().getSharedPreferences().getAll().toString());

            }else if (type.equals(null)){

                addPreferencesFromResource(R.xml.preference_default);
                Log.d(TAG, getPreferenceManager().getSharedPreferences().getAll().toString());

             }

            Preference ctc = findPreference(PREF_CT);
            if(ctc!=null) {
                if (sm.getSOSContact() == null) {
                    ctc.setSummary("Emergency Contact Not Configured");
                } else {
                    ctc.setSummary("Emergency Contact Configured\n" +
                            "Phone Number: " + sm.getSOSContact());
                }
            }

            ctc.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(i, CTC);
                    return true;
                }
            });
        }
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch (requestCode) {
                case CTC:
                    Cursor c = null;
                    String phno = "";
                    String ctname = "";
                    if (resultCode == Activity.RESULT_OK) {
                        Uri contactData = data.getData();
                        Log.i(TAG, "Contact Result Received: " + contactData.toString());

                        // get the contact's ID
                        String id = contactData.getLastPathSegment();

                        c = getActivity().getContentResolver().query
                                (ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +
                                                "=?", new String[] { id }, null);

                        int phnoIndx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);

                        if (c.moveToFirst()) {
                            phno = c.getString(phnoIndx).replace(" ","");

                            Log.d(TAG, "Got Phone Number: " + phno);
                            sm.setSOSContact(phno);

                            Preference ctcpref = findPreference(PREF_CT);
                            ctcpref.setSummary("Emergency Contact Configured\n" +
                                    "Phone Number: "+phno);
                        } else {
                            Log.w(TAG, "No Results Obtained");

                        }
                    }
            return;
            }
            super.onActivityResult(requestCode, resultCode, data);
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

            if(key.equals(PREF_BOUND)){
                if(sm.isGPSServiceRunning()) {
                    restartGPSTracking();
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
        private void restartGPSTracking(){

            if(sm.isGPSServiceRunning()){

                Intent intent = new Intent(getActivity(), GPSService.class);
                getActivity().stopService(intent);
                getActivity().startService(intent);
                Log.d(TAG, "Restarting Service: " + GPSService.class.getSimpleName());
                Toast.makeText(getActivity(), "Restarting GPS Tracking With New Boundary", Toast.LENGTH_SHORT)
                        .show();
            }

        }
    }
}