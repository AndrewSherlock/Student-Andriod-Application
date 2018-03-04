package com.itbstudentapp.NotificationSystem;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FirebaseNotificationManager {

    public static void sendNotificationToUser(Notification notification/*String user, String message */)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("notifications");

        String key = UUID.randomUUID().toString();

        Map userNotification = new HashMap();

        if(notification.getMessageSender() != null)
        {
            userNotification.put("user", notification.getMessageSender());
        }

        userNotification.put("type", notification.getNotificationType());
        userNotification.put("title", notification.getTitle());
        userNotification.put("body", notification.getBody());

//        userNotification.put("user", "b00090936");
//        userNotification.put("message", "here");
//
        reference.push().setValue(userNotification);
    }
}
