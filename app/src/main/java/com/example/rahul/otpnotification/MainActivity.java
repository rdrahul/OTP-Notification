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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference database;
    private Message message;
    private String TAG = "OTPMainActivity";
    private MessageAdapter messageAdapter;
    private RecyclerView recyclerView;
    private ArrayList<Message> allMessages ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //adding permission at runtime
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 0);
        }

        //get our firebase database object instance;
        database = FirebaseDatabase.getInstance().getReference();
        message = null;

        //recyclerView
        recyclerView = (RecyclerView) findViewById(R.id.rv_messages);

        //initializing our dataReference
        allMessages = new ArrayList<Message>();

        //set the layout manager
        LinearLayoutManager manager = new LinearLayoutManager(this );
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);

        //set the adapter
        messageAdapter = new MessageAdapter(allMessages);
        recyclerView.setAdapter(messageAdapter);


        RetrieveDataFromFirebase();

        /**
         * BindListener listens to the received messages and do what is necessary
         */
        SmsReceiver.BindListener(new SmsListener() {

            @Override
            public void MessageReceived(String messageText,  String messageSender) {

                //if the incoming message contains the otp display it
                String otp = GetIfContainsOTP( messageText );

                if ( otp != "" ){

                    //save the message and otp to a firebase realtime database
                    SaveToFirebase( messageText, messageSender, otp);

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

    private void SaveToFirebase(String messageBody ,String messageSender, String otp){

        //create the message oject and save to firebase
        message = new Message();
        message.setUid(database.child("OTP").push().getKey());
        message.setMessage(messageBody);
        message.setOtp(otp);
        message.setSender(messageSender);
        database.child("OTP").child(message.getUid()).setValue(message);

    }

    private void RetrieveDataFromFirebase(){
        database.child("OTP").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allMessages.clear();
                for (DataSnapshot messageDataSnapshot : dataSnapshot.getChildren()) {
                    Message message = messageDataSnapshot.getValue(Message.class);
                    allMessages.add(message);
                    Log.d(TAG, "onDataChange: " + message.getOtp());
                }
                Collections.reverse(allMessages);
                messageAdapter.dataChanged(allMessages);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
}
