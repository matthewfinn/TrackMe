package ie.nuigalway.trackme.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.LocationManager;
import android.util.Log;

import java.util.HashMap;


/**
 * Created by matthew on 17/01/2017.
 */

public class SessionManager {

    private static String TAG = SessionManager.class.getSimpleName();
    public static final String FNAME = "firstname";
    public static final String SURNAME = "surname";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";
    public static final String PHONE = "phone";
    public static final String TYPE = "type";
    public static final String TRACKME_SERVICE = "trackme_service";
    private static final String PREF = "TrackMePreferences";
    private static final String KEY_L = "LoggedIn";
    private static final String KEY_G = "GPSRunning";
    int MODE = 0; //private preferences mode used to set preference permissions

    SharedPreferences sp;
    Context ctx;
    Editor ed;


    public SessionManager(Context c){

        this.ctx = c;
        sp = ctx.getSharedPreferences(PREF, MODE);
        ed = sp.edit();
    }



    public void startLoginSession(boolean isLoggedIn, String fn, String sn, String em, String un, String ph) {

        ed.putBoolean(KEY_L, isLoggedIn);
        ed.putString(FNAME, fn);
        ed.putString(SURNAME, sn);
        ed.putString(EMAIL, em);
        ed.putString(USERNAME, un);
        ed.putString(PHONE, ph);

        // commit changes
        ed.commit();

        Log.d(TAG, "User login session modified. Session started for user.");
    }


    public boolean isLoggedIn(){

        return sp.getBoolean(KEY_L, false);
    }

    public void setGPSServiceRunning(boolean status){

        ed.putBoolean(TRACKME_SERVICE, status);
        ed.commit();
    }

    public boolean isGPSServiceRunning(){
        return sp.getBoolean(TRACKME_SERVICE, false);
    }

    public boolean hasLocationServiceOn(){

        LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            Log.d(TAG,"Application has location services enabled");
            return true;

        }
        Log.d(TAG,"Application has not got location services enabled");
        return false;
    }

    public boolean isGPSRunning(){
        return sp.getBoolean(KEY_G, false);
    }

    public void setGPSStatus(boolean b){

        ed.putBoolean(KEY_G,b);
        ed.commit();

        Log.d(TAG, "User login session modified. GPS Service Tracking Modified.");
    }

    public void setSOSContact(String con){


    }

    public String getSOSContact(){

        return null;
    }

    public void setProfileType(String type){


    }

    public String getProfileType(){

        return null;
    }


    public HashMap<String,String> getUserDetails(){

        HashMap<String, String> details = new HashMap<String, String>();
        // user name
        details.put(FNAME, sp.getString(FNAME, null));
        details.put(SURNAME, sp.getString(SURNAME, null));
        details.put(EMAIL, sp.getString(EMAIL, null));
        details.put(USERNAME, sp.getString(USERNAME, null));
        details.put(PHONE, sp.getString(PHONE, null));

        // return user details for session
        return details;
    }

    public void logOutUser(){

        ed.clear();
        ed.putBoolean(KEY_L,false);
        ed.commit();
        Log.d(TAG , sp.getAll().values().toString());
    }
}
