package ie.nuigalway.trackme.helper;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
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
    final String  TAG = WidgetHandler.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){

        SessionManager sm = new SessionManager(context);



      final int count = appWidgetIds.length;

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];
            //String number = String.format("%03d", (new Random().nextInt(900) + 100));

            Intent in;
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_trackme);


            if(sm.isLoggedIn()){
                in = new Intent (context, GPSService.class);
                if(!sm.isGPSServiceRunning()){

                    context.startService(in);
                    Log.d(TAG, "Starting Service: " + GPSService.class.getSimpleName());
                    Toast.makeText(context, "Starting GPS Tracking Service", Toast.LENGTH_LONG).show();
                    remoteViews.setTextViewText(R.id.actionButton,"Stop");
                }
                else if(sm.isGPSServiceRunning()){

                    context.stopService(in);
                    Log.d(TAG, "Stopping Service: " + GPSService.class.getSimpleName());
                    Toast.makeText(context, "Stopping GPS Tracking Service", Toast.LENGTH_LONG).show();
                    remoteViews.setTextViewText(R.id.actionButton,"Start");
                }
            }
            else{
                Toast.makeText(context, "Track Me Not Logged In. Please Log In", Toast.LENGTH_LONG).show();
            }


            Intent intent = new Intent(context, WidgetHandler.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.actionButton, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);

        }

    }
}


