package com.itbstudentapp;


import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MessageService extends Service{

    DatabaseReference ref;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStart();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onStart() {

        Log.e("Service Created", this.getClass().getName());

        String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        user = user.split("@")[0];

        ref = FirebaseDatabase.getInstance().getReference("users/" + user + "/messages/B00090936");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("MAIL SERVICE", "you have mail");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
