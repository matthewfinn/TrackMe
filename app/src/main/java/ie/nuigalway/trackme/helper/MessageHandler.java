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
    private SessionManager sm;

    public MessageHandler(Context c){
        ctx = c;
        sms = SmsManager.getDefault();
        sm = new SessionManager(ctx);
    }

    public void sendMessage(String message){

        String contact = sm.getSOSContact();

        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(message);
        sms.sendMultipartTextMessage(sm.getSOSContact(), null, parts, null, null);

        Log.d(TAG, "Emergency Message Being Sent");
    }



}
