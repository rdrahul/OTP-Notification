package com.example.rahul.otpnotification;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //adding permission at runtime
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {


                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECEIVE_SMS},
                        0);

        }


        SmsReceiver.BindListener(new SmsListener() {
            @Override
            public void MessageReceived(String messageText) {

                //if the incoming message contains the otp display it
                String otp = GetIfContainsOTP( messageText );

                if ( otp != "" ){
                    //create the notification
                    String spacedOtp = "";
                    for ( int i=0;i<otp.length() ; i++ ){
                        spacedOtp += otp.charAt(i);
                        spacedOtp += "";
                    }
                    CreateNotification( spacedOtp );
                }



            }
        });
    }

    /**
     * verify if a message contains an OTP or not
     * @param message
     */
    private String GetIfContainsOTP( String message){

        String OTPVal ="";
        //not implemented
        message = message.toLowerCase();
        if ( message.contains("otp") || message.contains("code") ){


            //find the code
            String pattern = "(\\d+){3,6}";

            // Create a Pattern object
            Pattern r = Pattern.compile(pattern);

            // Now create matcher object.
            Matcher m = r.matcher(message);

            if ( m.find()){
                OTPVal = m.group(0);
            }

        }
        return OTPVal;
    }


    //The function creates a notification
    public void CreateNotification( String message){

        //get the remoteViews object required in getting the contentView
        RemoteViews contentView = new RemoteViews(getPackageName() , R.layout.notication_content);
        contentView.setTextViewText(R.id.text, message);

        //the builder
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContent(contentView)
                .setContentTitle("something")
                .setPriority(Notification.PRIORITY_MAX);
                ;

        if (Build.VERSION.SDK_INT >= 21) mBuilder.setVibrate(new long[0]);

        //the result
        Intent result = new Intent(this , MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(result);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0 , PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent( resultPendingIntent );

        NotificationManager notificationManager = (NotificationManager)getSystemService( Context.NOTIFICATION_SERVICE );

        int mid = 1;
        notificationManager.notify( mid, mBuilder.build() );


    }
}
