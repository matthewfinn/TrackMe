package ie.nuigalway.trackme.services;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;

import ie.nuigalway.trackme.helper.MessageHandler;

import static java.lang.Math.sqrt;

public class FallDetectionService extends Service implements SensorEventListener {

    private static String TAG = FallDetectionService.class.getSimpleName();
    private SensorManager sensorManager;
    private Sensor sensor;
    private String previous, current;
    private static int bufferSize=80;
    static public double[] readings = new double[bufferSize];
    private double calc, rx, ry, rz, sigma=0.5, thresh1=10, thresh2=5, thresh3=2;


    private MessageHandler mh;

    @Override
    public void onCreate()
    {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Log.d(TAG, sensor.getName());
        initialise();
    }

    @Override public void onDestroy() {
        Log.d("Running onDestroy", "Stop Sensors & Current Background Service");
        sensorManager.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        return START_STICKY;
    }

    private void initialise(){


    }

//    private void getPosture(double[] w, double y){
//
//        int z = zeroCrossingRate(w);
//
//
//        if(z==0){
//            if(Math.abs(y)<thresh2){
//                current=ST;
//            }else{
//                current=SI;
//            }
//        }else{
//            if(z>thresh3){
//                current=WA;
//            }else{
//                current=NN;
//            }
//        }
//
//        Log.d(TAG,"Posture: "+current);
//    }

//    private int zeroCrossingRate(double[] w){
//
//        int count=0;
//        for(int i=1;i<=bufferSize-1;i++){
//
//            if((w[i]-thresh1)<sigma && (w[i-1]-thresh1)>sigma){
//                count=count+1;
//            }
//
//        }
//        return count;
//
//    }

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

            Log.d("Sensor", "Value: " + value);

            if ((value > 25) || (value < 1))  {
                //onActivateTimerActivity();
                viber(5000);
                onDestroy();
            }
        }
    }

    private void viber(int m){
        Vibrator oVibrate = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        if (oVibrate.hasVibrator()) { oVibrate.vibrate(m); }
    }

    private double computeReadings(double x, double y, double z){

        return sqrt((x*x)+(y*y)+(z*z));
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
