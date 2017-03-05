package ie.nuigalway.trackme.helper;

/**
 * Created by matthew on 25/02/2017.
 */



import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class GPSHelper implements LocationListener{


    private static String TAG = GPSHelper.class.getName();
    protected LocationManager lm; // Declaring a Location Manager
    private  Context c;
   // double lng, lat;
    Location loc;
    private String provider;
    private LatLng currentLocation;
    private boolean gpsEnabled, netEnabled;


    public GPSHelper(Context ctx){
        this.c = ctx;
    }


    public LatLng getCurrentStaticLocation() throws NullPointerException{
        try {
            lm = (LocationManager) c.getSystemService(c.LOCATION_SERVICE);

            gpsEnabled = lm.isProviderEnabled(lm.GPS_PROVIDER); // gps status
            Log.d(this.getClass().toString()+" | GPS Enabled", String.valueOf(gpsEnabled));

            netEnabled = lm.isProviderEnabled(lm.NETWORK_PROVIDER); // net status
            Log.d(this.getClass().toString()+" | Network Enabled", String.valueOf(netEnabled));

            if(gpsEnabled){
                provider = lm.GPS_PROVIDER;
            }
            else if(netEnabled){
                gpsEnabled = true;
                provider = lm.NETWORK_PROVIDER;
            }else{

                Log.d(this.getClass().toString(),"Nothing is enabled");
            }

            try{
                if(!provider.isEmpty()) {

                    lm.requestSingleUpdate(provider, this, null );
//                   try{
//
//                       Thread.sleep(2000);
//                   }catch (InterruptedException e){
//                       Log.e(TAG,"Waiting");
//                   }
                    loc = lm.getLastKnownLocation(provider);

                    currentLocation = new LatLng(loc.getLatitude(), loc.getLongitude());

                    Log.d(TAG+ " | Current Location" ,currentLocation.toString());

                    return currentLocation;
                }
            }catch(NullPointerException e){
               Log.e(TAG, "Provider not initialised because no provider accessible");
            }

        }catch (SecurityException e){
            Log.e(TAG, "Impossible to connect to LocationManager", e);
        }


        return null;
    }

    @NonNull
    public String getAddressString(LatLng loc) throws IOException {

        //Reverse Geocoding to get address string
        Geocoder geocoder = new Geocoder(c, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1);
        StringBuilder sb = new StringBuilder();
        if (addresses.size() > 0) {
            Address address = addresses.get(0);
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                sb.append(address.getAddressLine(i)).append(", ");
            sb.append(address.getCountryName());
        }
        return sb.toString();
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


