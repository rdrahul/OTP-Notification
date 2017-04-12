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

public class SmsReceiver extends BroadcastReceiver {

    private static SmsListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle data = intent.getExtras();

        Object[] pdus = (Object[]) data.get("pdus");
        String format  = data.getString("format");

        for ( int i=0;i<pdus.length;i++){
            SmsMessage smsMessage;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i], format);
            }
            else  {
                smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }



            String sender = smsMessage.getDisplayOriginatingAddress();
            //You must check here if the sender is your provider and not another one with same text.

            String messageBody = smsMessage.getMessageBody();

            //Pass on the text to our listener.
            mListener.MessageReceived(messageBody);


        }

    }

    public static void BindListener(SmsListener listener){
        mListener = listener;
    }
}

