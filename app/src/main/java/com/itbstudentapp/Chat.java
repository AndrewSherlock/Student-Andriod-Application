package com.itbstudentapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;

public class Chat extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout message_scrollview;
    private Button new_message;
    private Chat instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        instance = this;
        new_message = (Button) findViewById(R.id.new_message);
        new_message.setOnClickListener(this);

        message_scrollview = (LinearLayout) findViewById(R.id.message_list);

        getUsersMessages();
    }

    private void getUsersMessages()
    {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users/" + UtilityFunctions.getUserNameFromFirebase() +"/messages/");
        db.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {

                Message message = dataSnapshot.child(String.valueOf(dataSnapshot.getChildrenCount() - 1)).getValue(Message.class); // will break it
                View v = LayoutInflater.from(instance).inflate(R.layout.message_preview, null);

                Log.e("TEST_CHECK", "MESSAGE->NEW->LOOK");

                TextView text =  v.findViewById(R.id.message_sender);
                text.setText(dataSnapshot.getKey());

                TextView text_2 = v.findViewById(R.id.message_preview);
                text_2.setText(message.message);

                if(v.getParent() != null)
                {
                    ((ViewGroup) v.getParent()).removeView(v);
                }

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("message id", String.valueOf( dataSnapshot.getKey()));
                        LoadMessage(dataSnapshot.getKey());
                    }
                });

                message_scrollview.addView(v);

            }


            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void  LoadMessage(String messageID)
    {
        Intent intent = new Intent(this, MessageScreen.class);
        intent.putExtra("message_id", messageID);
        startActivity(intent);
    }



    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.new_message)
        {
            Intent intent = new Intent(this, MessageScreen.class);
            intent.putExtra("message_id", "none");
            startActivity(intent);
        }
    }
}

