package ie.nuigalway.trackme.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ie.nuigalway.trackme.fragment.HomeFragment;
import ie.nuigalway.trackme.helper.GPSHelper;
import ie.nuigalway.trackme.helper.LocalDBHandler;

public class GPSService extends Service {

    private static final String TAG = GPSService.class.getSimpleName();

    private static final String IDENTIFIER = "Location";
    private static final String KEY = "locData";

    private static final String LAT = "latData";
    private static final String LNG = "lngData";
    private static final String CDT = "timeData";
    private static final String PREF = "TrackMePreferences";
    int MODE = 0; //private preferences mode used to set preference permissions
    private static final int L_INT = 10000; //
    private static final float L_DIST = 0; //Cast to float, compiler understands to treat as fp num
    private LocationManager lm = null;
    private GPSHelper gh;
    private LocalBroadcastManager broadcaster;
    private String address;
    private Context ctx;

    private SharedPreferences sp;
    private Editor ed; //May not be needed
    private LocalDBHandler db;
    private HomeFragment h;


    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;


        public LocationListener(String provider)
        {
            Log.i(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {

            double lat = location.getLatitude();
            double lng = location.getLongitude();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
            String cdt = sdf.format(new Date());
            LatLng lCurr = new LatLng(lat,lng);

            if(gh.checkInternetServiceAvailable()){
                try {
                    address = gh.getAddressString(lCurr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{

                address = location.toString();
            }
            Intent intent = new Intent(IDENTIFIER); //FILTER is a string to identify this intent
            //intent.putExtra(KEY,lat+","+lng+","+cdt);

            intent.putExtra(LAT,lat);
            intent.putExtra(LNG,lng);
            intent.putExtra(CDT, cdt);
            sendBroadcast(intent);

            Log.i(TAG, "onLocationChanged: " + address);
            mLastLocation.set(location);

            Log.i(TAG,"Sending Location Data To Update UI");

        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.w(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.i(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.i(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate()
    {
        Log.i(TAG, "onCreate");
        gh = new GPSHelper(GPSService.this);
        initializeLocationManager();
        broadcaster = LocalBroadcastManager.getInstance(this);

        try {
            lm.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, L_INT, L_DIST,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.e(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            lm.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, L_INT, L_DIST,
                    mLocationListeners[0]);

        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy()
    {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        if (lm != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    lm.removeUpdates(mLocationListeners[i]);
                } catch (SecurityException ex) {
                    Log.i(TAG, "fail to remove location listeners, ignore", ex);
                }
            }
        }

    }

    private void initializeLocationManager() {
        Log.i(TAG, "initializeLocationManager");
        if (lm == null) {
            lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // Shared preferences got. to be used to get user preference for location tracking
        // i.e. "sp.getString("firstname",null)" gets the 'value' stored for 'key' firstname
        this.ctx = getApplicationContext();
        sp = ctx.getSharedPreferences(PREF,MODE);

        Log.i(TAG, "onStartCommand ");

        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }



}
