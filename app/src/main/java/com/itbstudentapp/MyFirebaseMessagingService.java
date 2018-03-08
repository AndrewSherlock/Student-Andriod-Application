package com.itbstudentapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itbstudentapp.utils.UserSettings;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

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

        Map<String, String> params = remoteMessage.getData();

        for(Map.Entry map : params.entrySet())
        {
            Log.e("", "onMessageReceived: " + map.getValue() + " Value <-/->Key " + map.getKey() );
        }

        if(params.get("type").equalsIgnoreCase("event"))
            setupNotifyOfNewEvent(params);
        else if(params.get("type").equalsIgnoreCase("chat"))
            setupNotifyOfNewMessage(params);
        else if(params.get("type").equalsIgnoreCase("forum"))
            setupNotifyOfNewForumPost(params);

    }

    private void setupNotifyOfNewForumPost(Map<String, String> params) {
    }

    private void setupNotifyOfNewMessage(Map<String, String> params)
    {
        if(notificationManager == null)
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);
        nBuilder.setSmallIcon(R.drawable.ic_launcher_web);

        setNotificationForUserSettings(nBuilder);
        nBuilder.setContentText(params.get("body"));
        getUserNameFromFirebase(params.get("title"), nBuilder);
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

    private void setupNotifyOfNewEvent(Map<String, String> details)
    {
        if(notificationManager == null)
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_web);

        setNotificationForUserSettings(nBuilder);

        nBuilder.setContentTitle("New event posted");
        nBuilder.setContentText(details.get("title"));

        notificationManager.notify(generateID(), nBuilder.build());
    }

    private void setNotificationForUserSettings(NotificationCompat.Builder nBuilder)
    {
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
    }

    private int generateID()
    {
        Random random = new Random();
        int divider = random.nextInt(10) + 1;

        Date date = new Date();
        int randId = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(date)) / divider;

        return randId;
    }
}
