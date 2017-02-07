package ie.nuigalway.trackme.application;

/**
 * Created by matthew on 17/01/2017.
 * Class that creates core Volley objects to handle data transmission.
 */
import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class App extends Application {

    public final static String TAG = App.class.getSimpleName();
    private static App app;
    private RequestQueue rq;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public static synchronized App getInstance() {
        return app;
    }

    public RequestQueue getRQ() {
        if (rq == null) {
            rq = Volley.newRequestQueue(getApplicationContext());
        }

        return rq;
    }

    public <T> void addToRQ(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRQ().add(req);
    }

    public <T> void addToTQ(Request<T> req) {
        req.setTag(TAG);
        getRQ().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (rq != null) {
            rq.cancelAll(tag);
        }
    }
}
