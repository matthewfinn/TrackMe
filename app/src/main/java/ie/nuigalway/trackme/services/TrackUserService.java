package ie.nuigalway.trackme.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TrackUserService extends Service {
    public TrackUserService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
