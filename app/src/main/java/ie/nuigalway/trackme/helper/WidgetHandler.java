package ie.nuigalway.trackme.helper;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import ie.nuigalway.trackme.R;
import ie.nuigalway.trackme.activity.Splash;
import ie.nuigalway.trackme.services.GPSService;

/**
 * Created by matthew on 13/03/2017.
 */

public class WidgetHandler extends AppWidgetProvider {
    private static final String TAG = WidgetHandler.class.getSimpleName();
    private static final String GPSTAG = GPSService.class.getSimpleName();
    private static final String MyOnClick1 = "myOnClickTag1";

    AppWidgetManager ap;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        SessionManager sm = new SessionManager(context);
        Intent in;

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_trackme);

        Log.d(TAG,"Intent: "+intent.getAction());

        if (MyOnClick1.equals(intent.getAction())) {

            Log.d(TAG,"ONCLICK1");
            if(sm.isLoggedIn()){
                in = new Intent (context, GPSService.class);

                if(!sm.isGPSServiceRunning()){

                    context.startService(in);
                    Log.d(TAG, "Starting Service: " + GPSTAG);
                    Toast.makeText(context, "Starting GPS Tracking Service", Toast.LENGTH_LONG).show();
                    rv.setTextViewText(R.id.actionButton, "||");
                    AppWidgetManager.getInstance(context).updateAppWidget(
                            new ComponentName(context, WidgetHandler.class), rv);
                }
                else if(sm.isGPSServiceRunning()){

                    context.stopService(in);
                    Log.d(TAG, "Stopping Service: " + GPSTAG);
                    Toast.makeText(context, "Stopping GPS Tracking Service", Toast.LENGTH_LONG).show();
                    rv.setTextViewText(R.id.actionButton, "►");
                    AppWidgetManager.getInstance(context).updateAppWidget(
                            new ComponentName(context, WidgetHandler.class),rv);
                }
            }
            else{
                Toast.makeText(context, "Track Me Not Logged In. Please Log In", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){

        ComponentName thisWidget = new ComponentName(context, WidgetHandler.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        Intent intent = new Intent(context, Splash.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        ap = appWidgetManager;


        for (int widgetId : allWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_trackme);
            remoteViews.setOnClickPendingIntent(R.id.actionButton, getPendingSelfIntent(context, MyOnClick1));
            remoteViews.setOnClickPendingIntent(R.id.trackMeImage, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);

        }

    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}


