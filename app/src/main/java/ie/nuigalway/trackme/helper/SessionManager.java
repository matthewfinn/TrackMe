package ie.nuigalway.trackme.helper;

import android.util.Log;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Context;

/**
 * Created by matthew on 17/01/2017.
 */

public class SessionManager {

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

    public void setLogin(boolean isLoggedIn) {

        ed.putBoolean(KEY_L, isLoggedIn);

        // commit changes
        ed.commit();

        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn(){
        return sp.getBoolean(KEY_L, false);
    }
}
