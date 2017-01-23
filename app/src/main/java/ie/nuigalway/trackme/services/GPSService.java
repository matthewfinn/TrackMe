package ie.nuigalway.trackme.services;

import android.app.Service;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;

public class GPSService extends Service {

    private static final String TAG = "GPSLOCATIONLISTENER";
    private LocationManager lm = null;
    private static final int LOCATION_INTERVAL = 200;
    private static final float LOCATION_DISTANCE = 10f;


    public GPSService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
