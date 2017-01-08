package ie.nuigalway.trackme;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LocalDbService extends Service {
    public LocalDbService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
