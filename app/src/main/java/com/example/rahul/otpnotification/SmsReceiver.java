package com.example.rahul.otpnotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * Created by Rahul on 4/13/2017.
 */

/**
 * listens to the new messages and extracts the otp
 */
public class SmsReceiver extends BroadcastReceiver {

    private static SmsListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {

        //get the data from the intent
        Bundle data = intent.getExtras();

        Object[] pdus = (Object[]) data.get("pdus");
        String format  = data.getString("format");

        for ( int i=0;i<pdus.length;i++){

            SmsMessage smsMessage;

            //run a version check
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i], format);
            }
            else  {
                smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }


            String sender = smsMessage.getDisplayOriginatingAddress();

            String messageBody = smsMessage.getMessageBody();

            //Pass on the text to our listener.
            mListener.MessageReceived(messageBody , sender);

        }

    }

    public static void BindListener(SmsListener listener){
        mListener = listener;
    }
}

