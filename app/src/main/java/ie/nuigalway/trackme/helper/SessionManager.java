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
    public static final String UID = "unique_id";
    public static final String SURNAME = "surname";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";
    public static final String PHONE = "phone";
    public static final String TYPE = "type";
    public static final String SOSCTC = "soscontact";

    public static final String TRACKME_SERVICE = "trackme_service";
    private static final String PREF = "TrackMePreferences";
    private static final String KEY_L = "LoggedIn";
    private static final String KEY_F = "FDRunning";

    private static final String PREF_FD = "runFD";
    private static final String PREF_LOCUP = "locUP";

    private static final String PREF_BOUND = "boundary";


    int MODE = 0; //private preferences mode used to set preference permissions

    SharedPreferences sp;
    Context ctx;
    Editor ed;


    public SessionManager(Context c){

        this.ctx = c;
        sp = ctx.getSharedPreferences(PREF, MODE);
        ed = sp.edit();
    }

    //Start Login Session
    public void startLoginSession(boolean isLoggedIn, String fn, String sn, String em, String un, String ph, String ty, String uid) {
        ed.putBoolean(KEY_L, isLoggedIn);
        ed.putString(FNAME, fn);
        ed.putString(SURNAME, sn);
        ed.putString(EMAIL, em);
        ed.putString(USERNAME, un);
        ed.putString(PHONE, ph);
        ed.putString(TYPE,ty);
        ed.putString(UID, uid);
        // commit changes
        ed.commit();
        Log.d(TAG, "User login session modified. Session started for user.");
    }

    //End Login Session
    public void logOutUser(){

        ed.clear();
        ed.putBoolean(KEY_L,false);
        ed.commit();
        Log.d(TAG , sp.getAll().values().toString());
    }


    //Login SP Getter
    public boolean isLoggedIn(){
        return sp.getBoolean(KEY_L, false);
    }

    //GPS Service SP Getter/Setter
    public boolean isGPSServiceRunning(){
        return sp.getBoolean(TRACKME_SERVICE, false);
    }
    public void setGPSServiceRunning(boolean status){
        ed.putBoolean(TRACKME_SERVICE, status);
        ed.commit();
    }

    //Method to find if Location Services are "on"/enabled.
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

    //SOS Contact Number Getter/Setter
    public String getSOSContact(){
        return sp.getString(SOSCTC, null);
    }
    public void setSOSContact(String con){

        ed.putString(SOSCTC, con);
        ed.commit();
    }

    //
    public String getProfileType(){
        return sp.getString(TYPE, null);
    }
    public void setProfileType(String type){
        ed.putString(TYPE, type);
        ed.commit();
    }

    public boolean getFD(){
        return sp.getBoolean(PREF_FD, false);
    }
    public void setFD(boolean fd){
        ed.putBoolean(TYPE, fd);
        ed.commit();
    }

    public String getUsername(){
        return sp.getString(USERNAME, null);
    }
    public void setUsername(String type){
        ed.putString(USERNAME, type);
        ed.commit();
    }

    public float getBoundary(){
        return sp.getFloat(PREF_BOUND, 1);
    }

    public void setBoundary(float b){
        ed.putFloat(PREF_BOUND, b);
        ed.commit();
    }

    public HashMap<String,String> getUserDetails(){

        HashMap<String, String> details = new HashMap<String, String>();
        // user name
        details.put(FNAME, sp.getString(FNAME, null));
        details.put(SURNAME, sp.getString(SURNAME, null));
        details.put(EMAIL, sp.getString(EMAIL, null));
        details.put(USERNAME, sp.getString(USERNAME, null));
        details.put(PHONE, sp.getString(PHONE, null));
        details.put(TYPE, sp.getString(TYPE, null));
        details.put(UID, sp.getString(UID, null));


        // return user details for session
        return details;
    }


}
