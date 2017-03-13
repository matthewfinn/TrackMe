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
import ie.nuigalway.trackme.services.GPSService;

/**
 * Created by matthew on 13/03/2017.
 */

public class WidgetHandler extends AppWidgetProvider {
    private static final String TAG = WidgetHandler.class.getSimpleName();
    private static final String MyOnClick1 = "myOnClickTag1";
    private static final String MyOnClick2 = "myOnClickTag2";
    private static final String MyOnClick3 = "myOnClickTag3";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        SessionManager sm = new SessionManager(context);
        Intent in;

        RemoteViews btn = new RemoteViews(context.getPackageName(),
                R.layout.widget_trackme);

        Log.d(TAG,"Intent: "+intent.getAction());

        if (MyOnClick1.equals(intent.getAction())) {
            Log.d(TAG,"ONCLICK1");
            if(sm.isLoggedIn()){
                in = new Intent (context, GPSService.class);
                if(!sm.isGPSServiceRunning()){
                    Log.d(TAG,"Should be false: " +String.valueOf(sm.isGPSServiceRunning()));
                    context.startService(in);
                    Log.d(TAG, "Starting Service: " + GPSService.class.getSimpleName());
                    btn.setTextViewText(R.id.actionButton, "Stop");
                    Toast.makeText(context, "Starting GPS Tracking Service", Toast.LENGTH_LONG).show();
                }
                else if(sm.isGPSServiceRunning()){
                    Log.d(TAG,"Should be true: " +String.valueOf(sm.isGPSServiceRunning()));
                    context.stopService(in);
                    btn.setTextViewText(R.id.actionButton, "Start");
                    Log.d(TAG, "Stopping Service: " + GPSService.class.getSimpleName());
                    Toast.makeText(context, "Stopping GPS Tracking Service", Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(context, "Track Me Not Logged In. Please Log In", Toast.LENGTH_LONG).show();
            }

        } else if (MyOnClick2.equals(intent.getAction())) {
            Toast.makeText(context, "Button2", Toast.LENGTH_LONG).show();
            Log.w("Widget", "Clicked button2");
        } else if (MyOnClick3.equals(intent.getAction())) {
            Toast.makeText(context, "Button3", Toast.LENGTH_LONG).show();
            Log.w("Widget", "Clicked button3");
        }



    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){

        ComponentName thisWidget = new ComponentName(context, WidgetHandler.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int widgetId : allWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_trackme);
            remoteViews.setOnClickPendingIntent(R.id.actionButton, getPendingSelfIntent(context, MyOnClick1));
            remoteViews.setOnClickPendingIntent(R.id.trackMeTV, getPendingSelfIntent(context, MyOnClick2));
            remoteViews.setOnClickPendingIntent(R.id.trackMeImage, getPendingSelfIntent(context, MyOnClick3));
            appWidgetManager.updateAppWidget(widgetId, remoteViews);

        }

    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}


