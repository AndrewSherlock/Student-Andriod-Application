package com.itbstudentapp;

import android.app.Service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;

/**
 * Created by andrew on 03/03/2018.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService
{
    @Override
    public void onTokenRefresh()
    {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();

        if(UtilityFunctions.getUserNameFromFirebase() != null)
            FirebaseDatabase.getInstance().getReference("users").child(UtilityFunctions.getUserNameFromFirebase()).child("instance_id").setValue(token);
    }

    public static void saveTokenToDb()
    {
        String token = FirebaseInstanceId.getInstance().getToken();
        FirebaseDatabase.getInstance().getReference("users").child(UtilityFunctions.getUserNameFromFirebase()).child("instance_id").setValue(token);
    }


}
