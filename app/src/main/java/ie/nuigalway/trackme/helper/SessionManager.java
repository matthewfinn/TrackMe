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

    public static final String FNAME = "firstname";
    public static final String SURNAME = "surname";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";
    private static final String PREF = "TrackMePreferences";
    private static final String KEY_L = "LoggedIn";
    private static String TAG = SessionManager.class.getSimpleName();
    int MODE = 0; //private preferences mode used to set preference permissions

    SharedPreferences sp;
    Context ctx;
    Editor ed;


    public SessionManager(Context c){

        this.ctx = c;
        sp = ctx.getSharedPreferences(PREF, MODE);
        ed = sp.edit();
    }



    public void startLoginSession(boolean isLoggedIn, String fn, String sn, String em, String ph) {

        ed.putBoolean(KEY_L, isLoggedIn);
        ed.putString(FNAME, fn);
        ed.putString(SURNAME, sn);
        ed.putString(EMAIL, em);
        ed.putString(PHONE, ph);

        // commit changes
        ed.commit();

        Log.d(TAG, "User login session modified. Session started for user.");
    }

    public boolean isLoggedIn(){
        return sp.getBoolean(KEY_L, false);
    }

    public boolean hasLocationServiceOn(){

        LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            Log.d(TAG,"Application has location services enabled");
            return true;

        }
        Log.d(TAG,"Application has not got location services enabled");
        return false;
    }

//    public void checkLogIn(){
//        if(!this.isLoggedIn()){
//
//        }
//    }

    public HashMap<String,String> getUserDetails(){

        HashMap<String, String> details = new HashMap<String, String>();
        // user name
        details.put(FNAME, sp.getString(FNAME, null));
        details.put(SURNAME, sp.getString(SURNAME, null));
        details.put(EMAIL, sp.getString(EMAIL, null));
        details.put(PHONE, sp.getString(PHONE, null));

        // return user details for session
        return details;
    }

    public void logOutUser(){

        ed.clear();
        ed.commit();
        Log.d(TAG ,sp.toString());
    }
}
