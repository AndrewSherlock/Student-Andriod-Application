package com.itbstudentapp.NotificationSystem;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by andrew on 03/03/2018.
 */

public class FirebaseNotificationManager {

    public static void sendNotificationToUser(String user, String message)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("notifications");
        Map userNotification = new HashMap();
        userNotification.put("user", user);
        userNotification.put("message", message);

        reference.push().setValue(userNotification);
    }
}
