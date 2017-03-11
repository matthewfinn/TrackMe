package ie.nuigalway.trackme.helper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ie.nuigalway.trackme.application.App;
import ie.nuigalway.trackme.application.AppConfig;

/**
 * Created by matthew on 08/03/2017.
 */

public class CloudDBHandler {

    private static final String TAG = CloudDBHandler.class.getSimpleName();
    private static final String UID = "uid";
    private static final String EM = "email";
    private static final String LAT = "latitude";
    private static final String LON = "longitude";
    private static final String TS = "timestamp";
    private LocalDBHandler ldb;
    private Context c;


    public CloudDBHandler(Context ctx){
        this.c = ctx;
        ldb = new LocalDBHandler(c);
    }

    public void addLatestLocation( final String lat, final String lon, final String ts){

        HashMap<String,String> uDetails = ldb.getUserDetails();
        final String uid = uDetails.get("unique_id");
        final String email = uDetails.get("email");
        String req = "req_loc";

        StringRequest r = new StringRequest(Request.Method.POST, AppConfig.LOCATION_URL, new Response.Listener<String>(){
            @Override
            public void onResponse(String res) {

                Log.d(TAG,"Response "+res.toString());

                try {

                    JSONObject j = new JSONObject(res);
                    boolean error = j.getBoolean("error");
                    Log.d(TAG, "JSON Obj: "+j.toString());
                    if (!error) {

                        //Toast.makeText(c, "User Location Pushed To Cloud Server", Toast.LENGTH_LONG).show();
                        Log.d(TAG,"Location Data Pushed To Cloud Server");

                    } else {
                        String err = j.getString("error_msg");
                        Log.e(TAG,"Cloud Server Push failed : "+ err);
                        Toast.makeText(c, "Cloud Server Push Failed: "+err, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException jse) {


                    Log.e(TAG,"Cloud Server JSON Exception : "+ jse.getMessage());
                    Toast.makeText(c, "Cloud Server JSON Exception: " + jse.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError e){


                Log.e(TAG,"Cloud Push Error : "+ e.toString());
                Toast.makeText(c,"Cloud Server Error Response: "+ e.getMessage(), Toast.LENGTH_LONG).show();

            }

        }){

            @Override
            protected Map<String, String> getParams(){

                Map<String,String> p = new HashMap<>();
                p.put(UID, uid);
                p.put(EM,email);
                p.put(LAT, lat);
                p.put(LON, lon);
                p.put(TS, ts);

                Log.d(TAG,p.toString());
                return p;
            }
        };
        App.getInstance().addToRQ(r, req);
    }

    public HashMap<String,String> requestUserLocation(final String email){
        final String req = "req_userloc";

        StringRequest r = new StringRequest(Request.Method.POST, AppConfig.GET_LOCATION_URL, new Response.Listener<String>(){
            @Override
            public void onResponse(String res) {

                Log.d(TAG, req + " Response " + res.toString());

                try {

                    JSONObject j = new JSONObject(res);
                    boolean error = j.getBoolean("error");
                    Log.d(TAG, req + " JSON Obj: "+j.toString());
                    if (!error) {

                        JSONObject user = j.getJSONObject("userLocation");
                        String lon = user.getString(LON);
                        String lat = user.getString(LAT);
                        String ts = user.getString(TS);

                        Log.d(TAG,"User '" + email + "' Location Received From Server");

                    } else {
                        String err = j.getString("error_msg");
                        Log.e(TAG, req + ": Failed To Retrieve User Location From Server: "+ err);
                    }
                } catch (JSONException jse) {


                    Log.e(TAG,req + ": Cloud Server JSON Exception : "+ jse.getMessage());
                }

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError e){

                Log.e(TAG,req + ": Data Retrieval Error: "+ e.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams(){

                Map<String,String> p = new HashMap<>();
                p.put(EM,email);
                Log.d(TAG,req + ": POST Parameters: "+p.toString());
                return p;
            }
        };
        App.getInstance().addToRQ(r, req);
        return null;
    }
}
