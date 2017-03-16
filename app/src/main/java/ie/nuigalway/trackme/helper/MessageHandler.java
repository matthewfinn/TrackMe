package ie.nuigalway.trackme.helper;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;

/**
 * Created by matthew on 05/03/2017.
 */

public class MessageHandler {

    private static String TAG = MessageHandler.class.getSimpleName();
    private SmsManager sms;
    Context ctx;
    private GPSHelper gh;

    public MessageHandler(Context c){

        ctx = c;
        sms = SmsManager.getDefault();


    }

    public void sendMessage(){

        Log.d(TAG, "Emergency Message Being Sent");
        sms.sendTextMessage("0867354231",null, "hi",null,null);
    }

}
