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
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class GPSHandler implements LocationListener{

    private static String TAG = GPSHandler.class.getSimpleName();
    private Context c;
    private LocationManager lm; // Declaring a Location Manager
    private Location loc;
    private String provider;
    private LatLng currentLocation;
    private boolean gpsEnabled, netEnabled;

    public GPSHandler(Context ctx){
        this.c = ctx;
    }


    public LatLng getCurrentStaticLocation() throws NullPointerException{
        try {
            lm = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);

            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER); // gps status
            Log.i(TAG+" | GPS Enabled", String.valueOf(gpsEnabled));

            netEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER); // net status
            Log.i(TAG+" | Network Enabled", String.valueOf(netEnabled));

            if(gpsEnabled){
                provider = LocationManager.GPS_PROVIDER;
            }
            else if(netEnabled){
                gpsEnabled = true;
                provider = LocationManager.NETWORK_PROVIDER;
            }else{

                Log.w(TAG,"Nothing is enabled");
            }

            try{
                if(!provider.isEmpty()) {

                    lm.requestSingleUpdate(provider, this, null );

                    loc = lm.getLastKnownLocation(provider);

                    currentLocation = new LatLng(loc.getLatitude(), loc.getLongitude());

                    Log.i(TAG, "Current Location Updated");

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

    public boolean checkInternetServiceAvailable(){

        boolean status;

        ConnectivityManager connMgr= (ConnectivityManager)
                c.getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isConnected()) {
            status = true;
        }else{
            status = mobile.isConnected();
        }

        return status;
    }

    public String getShortAddressString(LatLng loc) throws IOException{
        String add = loc.toString();
        if(checkInternetServiceAvailable()==true){
            Geocoder geocoder = new Geocoder(c, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1);
                StringBuilder sb = new StringBuilder();
                if (!addresses.isEmpty()) {
                    Address address = addresses.get(0);

                    add = address.getAddressLine(0);
                }
            }catch (IOException e){

                Toast.makeText(c, "Could not connect to Geocoder to get address string,\n Connection timed out" , Toast.LENGTH_LONG).show();
            }
        }

        return add;
    }

    @NonNull
    public String getAddressString(LatLng loc) throws IOException {

        String add = loc.toString();

       if(checkInternetServiceAvailable()==true){
            Geocoder geocoder = new Geocoder(c, Locale.getDefault());
           try {
               List<Address> addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1);
               StringBuilder sb = new StringBuilder();
               if (!addresses.isEmpty()) {
                   Address address = addresses.get(0);
                   for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                       sb.append(address.getAddressLine(i)).append(",\n");
                   }
                   sb.append(address.getCountryName());

                   add = sb.toString();
               }
           }catch (IOException e){

               Toast.makeText(c, "Could not connect to Geocoder to get address string,\n Connection timed out" , Toast.LENGTH_LONG).show();
           }
       }

        return add;

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


