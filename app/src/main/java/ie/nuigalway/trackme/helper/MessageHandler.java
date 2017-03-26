package ie.nuigalway.trackme.helper;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by matthew on 05/03/2017.
 */

public class MessageHandler {

    private static String TAG = MessageHandler.class.getSimpleName();
    private SmsManager sms;
    Context ctx;
    private GPSHandler gh;

    public MessageHandler(Context c){

        ctx = c;
        sms = SmsManager.getDefault();


    }

    public void sendMessage(String message){


        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(message);
        sms.sendMultipartTextMessage("0867354231", null, parts, null, null);

        Log.d(TAG, "Emergency Message Being Sent");
    }



}
