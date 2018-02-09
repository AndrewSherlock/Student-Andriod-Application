package com.itbstudentapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Chat extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout message_scrollview;

    private TextView new_message;
    private TextView message_groups;

    private EditText search_messages;
    private Chat instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        instance = this;

        new_message = (TextView) findViewById(R.id.chat_new_message_button);
        new_message.setOnClickListener(this);

        message_groups = (TextView) findViewById(R.id.chat_message_groups);
        message_groups.setOnClickListener(this);

        //search_messages.addTextChangedListener();

        message_scrollview = (LinearLayout) findViewById(R.id.message_list);


        getUsersMessages();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(message_scrollview.getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

    }

    private void getUsersMessages() {
        final DatabaseReference db = FirebaseDatabase.getInstance()
                .getReference("users/" + UtilityFunctions.getUserNameFromFirebase() + "/messages/").orderByChild("time_stamp").getRef();

           db.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {

                Message message = dataSnapshot.child(String.valueOf(dataSnapshot.getChildrenCount() - 2)).getValue(Message.class); // will break it

                final View v = LayoutInflater.from(instance).inflate(R.layout.message_preview, null);
                final LinearLayout linear = v.findViewById(R.id.message_bg);

                String read_status = dataSnapshot.child("read_status").getValue(String.class);
                if (read_status != null) {
                    if (read_status.equals("0")) {
                        linear.setBackgroundColor(getResources().getColor(R.color.unread_message));
                    }
                }

                TextView text = v.findViewById(R.id.message_sender);
                text.setText(dataSnapshot.getKey());

                TextView text_2 = v.findViewById(R.id.message_date);
                text_2.setText(UtilityFunctions.milliToTime(dataSnapshot.child("time_stamp").getValue(Long.class))); //TODO broken

                if (v.getParent() != null) {
                    ((ViewGroup) v.getParent()).removeView(v);
                }

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoadMessage(dataSnapshot.getKey());
                        dataSnapshot.child("read_status").getRef().setValue("1");
                        linear.setBackgroundColor(getResources().getColor(R.color.read_message));
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

    private void LoadMessage(String messageID) {
        Intent intent = new Intent(this, MessageScreen.class);
        intent.putExtra("message_id", messageID);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {


        if (v.getId() == new_message.getId()) {
            Intent intent = new Intent(this, MessageScreen.class);
            intent.putExtra("message_id", "none");
            startActivity(intent);
        }
    }
}

