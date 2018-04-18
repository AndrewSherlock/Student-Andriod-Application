package com.itbstudentapp;

import android.app.Service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService
{
    @Override
    public void onTokenRefresh()
    {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();

        // get instance id from db
        if(UtilityFunctions.getUserNameFromFirebase() != null)
            FirebaseDatabase.getInstance().getReference("users").child(UtilityFunctions.getUserNameFromFirebase()).child("instance_id").setValue(token);
    }

    // save instance id
    public static void saveTokenToDb()
    {
        String token = FirebaseInstanceId.getInstance().getToken();
        FirebaseDatabase.getInstance().getReference("users").child(UtilityFunctions.getUserNameFromFirebase()).child("instance_id").setValue(token);
    }


}
