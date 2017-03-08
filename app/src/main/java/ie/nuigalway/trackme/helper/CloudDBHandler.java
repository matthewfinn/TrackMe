package ie.nuigalway.trackme.helper;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ie.nuigalway.trackme.application.AppConfig;

/**
 * Created by matthew on 08/03/2017.
 */

public class CloudDBHandler {

    private static final String TAG = CloudDBHandler.class.getSimpleName();
    //private SessionManager sm;
    private LocalDBHandler db;
    private Context ctx;

    private static final String ID = "id";

    private static final String EMAIL = "email";

    private static final String UID = "unique_id";
    private static final String CR = "created_at";
    private static final String LAT = "latitude";
    private static final String LNG = "longitude";
    private static final String TS = "timestamp";

    public void CloudDBHandler(Context ctx){

        this.ctx = ctx;
       // this.sm = new SessionManager(ctx);
        this.db = new LocalDBHandler(ctx);

    }

    private void addLatestLocation(/*PUT STUFF HERE*/){


        db.getUserDetails();

        String req = "req_loc";

        StringRequest r = new StringRequest(Request.Method.POST, AppConfig.LOCATION_URL, new Response.Listener<String>(){
            @Override
            public void onResponse(String res) {

                Log.d(TAG, "Register Response: " + res);


                try {

                    JSONObject j = new JSONObject(res);
                    boolean error = j.getBoolean("error");
                    System.out.print(j.toString());

                    if (!error) {
//                        String uid = j.getString("uid");
//                        JSONObject user = j.getJSONObject("user");
//
//                        String fn = user.getString("first_name");
//                        String sn = user.getString("surname");
//                        String em = user.getString("email");
//                        String ph = user.getString("phone_no");
//                        String cr = user.getString("created_at");

                      //  Toast.makeText(getContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();



                    } else {
                        String err = j.getString("error_msg");
                      //  Toast.makeText(getContext(), "On Response: "+err, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException jse) {

                    jse.printStackTrace();
                   // Toast.makeText(getContext(), "On Response Error" + jse.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError e){

                Log.e(TAG,"Registration Error : "+ e.getMessage());
              //  Toast.makeText(getContext(),"On Error Response: "+ e.getMessage(), Toast.LENGTH_LONG).show();

            }

        }){

            @Override
            protected Map<String, String> getParams(){

                Map<String,String> p = new HashMap<>();
//                p.put("first_name", fn);
//                p.put("surname",sn);
//                p.put("phone_no", ph);
//                p.put("email", em);
//                p.put("password", pw);

                return p;
            }
        };


    }

}
