package ie.nuigalway.trackme.helper;

/**
 * Created by matthew on 25/02/2017.
 *
 *
 *
 *
 * THIS COULD BE A PRIVATE CLASS WITHIN THE HOME FRAGMENT??????/
 */



import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;


public class GPSHelper implements LocationListener{


    private static String TAG = GPSHelper.class.getName();
    protected LocationManager lm; // Declaring a Location Manager
    private  Context c;
    double lng, lat;
    Location loc;
    private String provider;
    private boolean gpsEnabled, netEnabled;


    public GPSHelper(Context ctx){
        this.c = ctx;
    }


    public LatLng getCurrentStaticLocation(){
        try {
            lm = (LocationManager) c.getSystemService(c.LOCATION_SERVICE);
            gpsEnabled = lm.isProviderEnabled(lm.GPS_PROVIDER); // gps status
            netEnabled = lm.isProviderEnabled(lm.NETWORK_PROVIDER); // net status

            if(gpsEnabled){
                provider = lm.GPS_PROVIDER;
            }
            else if(netEnabled){
                gpsEnabled = true;
                provider = lm.NETWORK_PROVIDER;
            }

            if(!provider.isEmpty()){

                //LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener
                lm.requestLocationUpdates(provider, 1000, 1, this);
                loc = lm.getLastKnownLocation(provider);

                return new LatLng(loc.getLatitude(),loc.getLongitude());
        }


        }catch (SecurityException e){
            Log.e(TAG, "Impossible to connect to LocationManager", e);
        }


        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
    }


    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}


