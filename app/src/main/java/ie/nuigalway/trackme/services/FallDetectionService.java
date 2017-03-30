package ie.nuigalway.trackme.services;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import ie.nuigalway.trackme.activity.MainActivity;
import ie.nuigalway.trackme.helper.GPSHandler;
import ie.nuigalway.trackme.helper.MessageHandler;
import ie.nuigalway.trackme.helper.SessionManager;

import static java.lang.Math.sqrt;

public class FallDetectionService extends Service implements SensorEventListener {

    private static String TAG = FallDetectionService.class.getSimpleName();
    public static final String CDT = "your_package_name.countdown_br";
    Intent bi = new Intent(CDT);
    private SensorManager sensorManager;
    private Sensor sensor;
    private double  rx, ry, rz;
    private CountDownTimer cdt;
    private GPSHandler gh;
    private MessageHandler mh;
    private SessionManager sh;
    private int timer;

    @Override
    public void onCreate()
    {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mh = new MessageHandler(getApplicationContext());
        gh = new GPSHandler(getApplicationContext());
        sh = new SessionManager(getApplicationContext());
        Log.d(TAG, sensor.getName());
        initialise();
    }

    @Override public void onDestroy() {
        Log.d("Running onDestroy", "Stop Sensors & Current Background Service");
        sensorManager.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent.getAction()!=null&&intent.getAction().equals("STOP")) {
            stopSelf();
            bi.putExtra("close",true);
            Log.d(TAG,"Stopping Service");
            cdt.cancel();
            return START_NOT_STICKY;

        }else{
            Log.d(TAG, "onStartCommand");
            super.onStartCommand(intent, flags, startId);
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);
            return START_NOT_STICKY;
        }
    }

    private void initialise(){
        
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){

            rx = event.values[0]; ry = event.values[1]; rz = event.values[2];
            double value = computeReadings(rx,ry,rz);
            Log.d("Sensor Value: ", String.valueOf(value));

            if ((value > 25) || (value < 1))  {

                Log.d(TAG,"Possible Fall Detected. Timer Started");


                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                startActivity(i);
                Log.d(TAG, "Attempting to start Intent: "+i.toString());

                cdt = new CountDownTimer(5000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                        long mil = millisUntilFinished/1000;
                        bi.putExtra("countdown", mil);
                        sendBroadcast(bi);
                    }

                    @Override
                    public void onFinish() {

                        Log.i(TAG, "Timer finished");

                        StringBuilder sb = new StringBuilder();
                        sb.append("Hi, "+sh.getUsername()+" here!\n");
                        sb.append("TrackMe has just detected a possible fall.");
                        sb.append("I may be in trouble.\n");
                        sb.append("My current location is approximately: \n");
                        try {
                            sb.append(gh.getAddressString(gh.getCurrentStaticLocation())+"\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        sb.append("If you could check in on me that'd be great \n");
                        sb.append("Thanks, "+ sh.getUsername());
                        Log.d(TAG, sb.toString());
                        mh.sendMessage(sb.toString());
                    }
                };

                cdt.start();

                onDestroy();
            }
        }
    }

    private double computeReadings(double x, double y, double z){

        return sqrt((x*x)+(y*y)+(z*z));
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
