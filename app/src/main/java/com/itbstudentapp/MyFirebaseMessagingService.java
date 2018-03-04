package com.itbstudentapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.itbstudentapp.utils.UserSettings;

import org.json.JSONObject;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{

    NotificationManager notificationManager;

    public MyFirebaseMessagingService()
    {
        super();
        FirebaseMessaging.getInstance().subscribeToTopic("user_"+ UtilityFunctions.getUserNameFromFirebase());
        FirebaseMessaging.getInstance().subscribeToTopic("events");
        Log.e("Here", "MyFirebaseMessagingService: " +  UtilityFunctions.getUserNameFromFirebase());
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);

        Log.e("Not", "onMessageReceived: " + "notification" );


        if(notificationManager == null)
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_background);

        if(UserSettings.play_sounds)
        {
            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            nBuilder.setSound(sound);
        }

        if(UserSettings.vibrate)
        {
            long[] pattern = {500,1000};
            nBuilder.setVibrate(pattern);
        }

        if(UserSettings.flash)
        {
            nBuilder.setLights(Color.GREEN, 500, 1000);
        }

        nBuilder.setAutoCancel(true);
        Intent notificationIntent = UserSettings.currentIntent;
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        nBuilder.setContentIntent(contentIntent);

        Map<String, String> params = remoteMessage.getData();
        Log.e(":/", "onMessageReceived: " +  remoteMessage.getData().get("type"));

        for(Map.Entry map : params.entrySet())
        {
            Log.e("Map", "onMessageReceived: " + map.getKey() + "/" + map.getValue() );
        }

        if(params.get("type").equalsIgnoreCase("event"))
        {
            nBuilder.setContentTitle("Event :" + params.get("title"));
            nBuilder.setContentText(params.get("body"));
        } else{

            if(params.get("type").equalsIgnoreCase("chat"))
            {
                nBuilder.setContentText(params.get("body"));
                getUserNameFromFirebase(params.get("title"), nBuilder);
                //nBuilder.setContentTitle(params.get("title"));
                return;
            } else{
                nBuilder.setContentTitle(params.get("New post in " + params.get("user")));
            }

            nBuilder.setContentText(params.get("body"));
        }
        notificationManager.notify(1, nBuilder.build());
    }

    private void getUserNameFromFirebase(final String userID, final NotificationCompat.Builder nBuilder)
    {
        final String username = userID.split(" ")[0];
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/" + username);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String[] name = dataSnapshot.child("username").getValue(String.class).split(" ");
                String formattedName = "";

                for(int i = 0; i < name.length; i++)
                {
                    formattedName += name[i].substring(0,1).toUpperCase() + name[i].substring(1, name[i].length()).toLowerCase() + " ";
                }

                nBuilder.setContentTitle(formattedName + "sent you a message!");
                notificationManager.notify(1, nBuilder.build());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
