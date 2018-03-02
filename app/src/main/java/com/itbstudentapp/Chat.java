package com.itbstudentapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.*;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout message_scrollview;

    private TextView new_message;
    private TextView message_groups;

    private EditText search_messages;
    private Chat instance;

    private ArrayList<View> views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        views = new ArrayList<>();
        instance = this;

        new_message = (TextView) findViewById(R.id.chat_new_message_button);
        new_message.setOnClickListener(this);

        message_groups = (TextView) findViewById(R.id.chat_message_groups);
        message_groups.setOnClickListener(this);

        //search_messages.addTextChangedListener();

        message_scrollview = (LinearLayout) findViewById(R.id.message_list);


        getUsersMessages();
        //addListenerToMessages();

    }

    private void getUsersMessages()
    {
        final DatabaseReference db = FirebaseDatabase.getInstance()
                .getReference("users/" + UtilityFunctions.getUserNameFromFirebase());//).orderByChild("time_stamp").getRef();

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<Long, DataSnapshot> chatDB = new TreeMap<Long, DataSnapshot>(Collections.<Long>reverseOrder());

                for(DataSnapshot data : dataSnapshot.child("messages").getChildren())
                {
                    chatDB.put(data.child("time_stamp").getValue(Long.class), data);
                }

                setUpMessages(chatDB);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setUpMessages(Map<Long, DataSnapshot> chatDB)
    {
        for (Map.Entry data : chatDB.entrySet())
        {
            final DataSnapshot snap = (DataSnapshot) data.getValue();
            snap.getRef().addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s)
                {
                    addMessageToView(dataSnapshot);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s)
                {
                    if(!dataSnapshot.getKey().equalsIgnoreCase("time_stamp"))
                        return;

                    for(View v : views)
                    {
                        if(v.getTag().toString().equalsIgnoreCase(snap.getKey()))
                        {

                            final LinearLayout linear = v.findViewById(R.id.message_bg);

                            String read_status = snap.child("read_status").getValue(String.class);
                            if (read_status != null) {
                                if (read_status.equals("0")) {
                                    linear.setBackgroundColor(getResources().getColor(R.color.unread_message));
                                }
                            }

                            TextView text_2 = v.findViewById(R.id.message_date);
                     //       text_2.setText(UtilityFunctions.milliToTime(snap.child("time_stamp").getValue(Long.class))); //TODO broken


                            message_scrollview.removeView(v);
                            message_scrollview.addView(v, 0);
                           // message_scrollview.findViewWithTag(dataSnapshot.getKey()).bringToFront();
                        }
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            addMessageToView(snap);
        }
    }

    private void addMessageToView(final DataSnapshot snap)
    {
        Message message = snap.child(String.valueOf(snap.getChildrenCount() - 2)).getValue(Message.class); // will break it

        final View v = LayoutInflater.from(instance).inflate(R.layout.message_preview, null);
        final LinearLayout linear = v.findViewById(R.id.message_bg);

        String read_status = snap.child("read_status").getValue(String.class);
        if (read_status != null) {
            if (read_status.equals("0")) {
                linear.setBackgroundColor(getResources().getColor(R.color.unread_message));
            }
        }

        TextView text = v.findViewById(R.id.message_sender);
        text.setText(snap.getKey());

        TextView text_2 = v.findViewById(R.id.message_date);
       // text_2.setText(UtilityFunctions.milliToTime(snap.child("time_stamp").getValue(Long.class)));

        CircleImageView circleImageView = v.findViewById(R.id.message_user_image);
        loadUserImage(circleImageView, snap.getKey());

        if (v.getParent() != null) {
            ((ViewGroup) v.getParent()).removeView(v);
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadMessage(snap.getKey());
                snap.child("read_status").getRef().setValue("1");
                linear.setBackgroundColor(getResources().getColor(R.color.read_message));
            }
        });

        v.setTag(snap.getKey());
        message_scrollview.addView(v);
        views.add(views.size(), v);
    }

    private void loadUserImage(final CircleImageView circleImageView, String userName)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users" + userName);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("imageLink") != null)
                {
                    String imageUrl = dataSnapshot.child("imageLink").getValue(String.class);
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("userImages/" + imageUrl);
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getBaseContext()).load(uri).into(circleImageView);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void messageListener()
    {

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

