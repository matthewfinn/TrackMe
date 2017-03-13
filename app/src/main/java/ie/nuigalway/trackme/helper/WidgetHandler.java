package ie.nuigalway.trackme.helper;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Random;

import ie.nuigalway.trackme.R;
import ie.nuigalway.trackme.services.GPSService;

/**
 * Created by matthew on 13/03/2017.
 */

public class WidgetHandler extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){

        final String  TAG = WidgetHandler.class.getSimpleName();
        SessionManager sm = new SessionManager(context);


      final int count = appWidgetIds.length;

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];
            String number = String.format("%03d", (new Random().nextInt(900) + 100));

            Intent gps = new Intent (context, GPSService.class);


            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_trackme);
            remoteViews.setTextViewText(R.id.textView, number);
            remoteViews.setTextViewText(R.id.actionButton,number);

            if(sm.isGPSServiceRunning()){
                Log.d(TAG, "GPS Running");
            }
            else{
                Log.d(TAG, "GPS Not Running");
                //remoteViews.setTextViewText(R.id.actionButton,number);
            }
            if(sm.isLoggedIn()){Log.d(TAG,"User Logged In");}
            else{Log.d(TAG,"Not Logged In");}

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


