package ie.nuigalway.trackme.services;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import ie.nuigalway.trackme.helper.MessageHandler;

import static java.lang.Math.sqrt;

public class FallDetectionService extends Service implements SensorEventListener {

    private static String TAG = FallDetectionService.class.getSimpleName();
    private static String FA = "falling";
    private static String ST = "standing";
    private static String SI = "sitting";
    private static String WA = "walking";
    private static String NN = "none";
    private SensorManager sensorManager;
    private String previous, current;
    private static int bufferSize=80;
    static public double[] readings = new double[bufferSize];
    private double calc, rx, ry, rz, sigma=0.5, thresh1=10, thresh2=5, thresh3=2;


    private MessageHandler mh;

    @Override
    public void onCreate()
    {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
        initialise();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private void initialise(){

        current = NN;
        previous= NN;
        for(int i=0;i<bufferSize;i++){
            readings[i]=0;
        }
    }

    private void getPosture(double[] w, double y){

        int z = zeroCrossingRate(w);

        if(z==0){
            if(Math.abs(y)<thresh2){
                current=ST;
            }else{
                current=SI;
            }
        }else{
            if(z>thresh3){
                current=WA;
            }else{
                current=NN;
            }
        }
    }

    private int zeroCrossingRate(double[] w){

        int count=0;
        for(int i=1;i<=bufferSize-1;i++){

            if((w[i]-thresh1)<sigma && (w[i-1]-thresh1)>sigma){
                count=count+1;
            }

        }
        return count;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){

            rx = event.values[0]; ry = event.values[1]; rx = event.values[2];
            computeReadings(rx,ry,rz);
            getPosture(readings,ry);
            detectFall(current, previous);
        }
    }

    private void computeReadings(double x, double y, double z){

        calc = sqrt((rx*rx)+(ry*ry)+(rz*rz));

        for(int i=0;i<=bufferSize-2;i++){
            readings[i]=readings[i+1];
        }
        readings[bufferSize-1]=calc;


    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void detectFall(String c, String p){

        if(!p.equalsIgnoreCase(c)){
            if(c.equalsIgnoreCase(FA)){
              //  m1_fall.start();
            }
            if(c.equalsIgnoreCase(SI)){
              //  m2_sit.start();
            }
            if(c.equalsIgnoreCase(ST)){
               // m3_stand.start();
            }
            if(c.equalsIgnoreCase(WA)){
               // m4_walk.start();
            }
        }


    }
}
