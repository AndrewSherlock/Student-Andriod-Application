package com.itbstudentapp;

import android.app.Dialog;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicReference;

/*
*  TODO: Multi user texts arnt working as of yet
*  TODO: Notifications arnt working,
*  TODO: When a user has no texts, tell them
*
* */

public class MessageScreen extends AppCompatActivity implements View.OnClickListener{

    private Button reciepentChooser, sendMessageButton;
    private EditText reciepentList, message_box;

    private EditText message_field;
    private RelativeLayout topBar;

    private ArrayList<String> messageRecievers;

    private MessageScreen instance;
    private boolean newMessage;

    private ArrayList<UserContactInformation> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_screen);
        Bundle b = getIntent().getExtras();

        messageRecievers = new ArrayList<String>();
        instance = this;

        message_field = (EditText) findViewById(R.id.message_text_box);
        topBar = (RelativeLayout) findViewById(R.id.message_title_bar);
        sendMessageButton = (Button) findViewById(R.id.message_send);
        sendMessageButton.setOnClickListener(this);
        message_box = (EditText) findViewById(R.id.message_text_box);

        messageRecievers.add(UtilityFunctions.getUserNameFromFirebase());

        if(b.getString("message_id").equals("none"))
        {
                // load in new message settings
            setupNewMessageSettings();
            users = new ArrayList<UserContactInformation>();
            getListOfUsers();
            newMessage = true;
        } else{
            View v = LayoutInflater.from(this).inflate(R.layout.message_top_bar, null);
            TextView text = v.findViewById(R.id.message_top_bar);
            text.setText(b.getString("message_id"));

            loadMessagesFromDatabase(b.getString("message_id"));
            getMessageRecievers(b.getString("message_id"));
            topBar.addView(v);
            newMessage = false;
        }
    }

    private void getMessageRecievers(String message_recievers)
    {
        if(message_recievers.contains(","))
        {
            String[] split = message_recievers.split(",");

            for (int i = 0; i < split.length; i++)
            {
                messageRecievers.add(split[i]);
            }
        } else{
            messageRecievers.add(message_recievers);
        }
    }

    private void setupNewMessageSettings()
    {
        View v = LayoutInflater.from(this).inflate(R.layout.new_message_bar, null);
        reciepentChooser = (Button) v.findViewById(R.id.choose_receiver);
        reciepentChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(instance);
                dialog.setTitle("Choose contacts");
                dialog.setContentView(R.layout.contact_list);
                LinearLayout linearLayout = dialog.findViewById(R.id.contact_scroll);

                for (int i = 0; i < users.size(); i++)
                {
                    if(users.get(i).user_number.equalsIgnoreCase(UtilityFunctions.getUserNameFromFirebase()))
                    {
                        continue;
                    }

                    final View view = LayoutInflater.from(instance).inflate(R.layout.contact_box, null);
                    final TextView user_full_name = view.findViewById(R.id.user_full_name);
                    user_full_name.setText(users.get(i).username);
                    final TextView user_number = view.findViewById(R.id.user_number);
                    user_number.setText(users.get(i).user_number);

                    if(messageRecievers.contains(users.get(i).user_number))
                    {
                        RadioButton radioButton = view.findViewById(R.id.user_information_radio);
                        radioButton.setChecked(true);
                    }

                    view.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {

                            RadioButton radioButton = v.findViewById(R.id.user_information_radio);
                            TextView user_number = v.findViewById(R.id.user_number);

                            if(radioButton.isChecked())
                            {
                                radioButton.setChecked(false);
                                RemoveUserFromList(user_number.getText().toString());
                            } else
                                {
                                    radioButton.setChecked(true);
                                    AddUserToList(user_number.getText().toString());
                                }

                            TextView text = v.findViewById(R.id.user_number);
                        }
                    });

                    linearLayout.addView(view);
                }


                Button button = dialog.findViewById(R.id.reciepentsChoosen);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText list = findViewById(R.id.receiver_option);
                        String currentUsers = "";
                        if(messageRecievers.size() > 1)
                        {
                            for(int i = 1; i < messageRecievers.size(); i++)
                            {
                                Log.e("current user", messageRecievers.get(i));
                                currentUsers += messageRecievers.get(i);

                                if(i < messageRecievers.size() - 1)
                                {
                                    currentUsers += ",";
                                }
                            }
                        }

                        list.setText(currentUsers);
                        dialog.hide();
                    }
                });
                dialog.show();
            }
        });
        reciepentList = (EditText) v.findViewById(R.id.receiver_option);
        topBar.addView(v);
    }

    private void AddUserToList(String user_name)
    {
        messageRecievers.add(user_name);
    }

    private void RemoveUserFromList(String user_name)
    {
        for(int i = 0; i < messageRecievers.size(); i++)
        {
            if(messageRecievers.get(i).equalsIgnoreCase(user_name))
            {
                messageRecievers.remove(i);
            }
        }
    }

    String currentMessageId;

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.message_send)
        {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("users/" + messageRecievers.get(0) +"/messages/" + messageRecievers.get(1));
            final Query lastQuery = db.orderByKey().limitToLast(1);

            lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(DataSnapshot ds) {
                    for(DataSnapshot snapshot : ds.getChildren()){
                        currentMessageId = snapshot.getKey();
                    }

                    if(currentMessageId == null)
                        currentMessageId = "-1";

                    SendMessage(Integer.parseInt(currentMessageId));

                    if(newMessage)
                    {
                       ReloadScreenToMessage(buildAddress(messageRecievers.get(0), messageRecievers));
                    }
                }

                public void onCancelled(DatabaseError databaseError) {}
            });

        }
    }

    private void loadMessagesFromDatabase(final String message_id)
    {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users/" + messageRecievers.get(0) +"/messages/" + message_id);
        final String[] colors = UtilityFunctions.getNumberOfColors(2); // hard coded
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                View v = LayoutInflater.from(instance).inflate(R.layout.message_dialog_box,null);
                Message message = dataSnapshot.getValue(Message.class);

                RelativeLayout dialog_box = v.findViewById(R.id.dialog_box);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dialog_box.getLayoutParams();

                Log.e("child added", "new message from : " + message.sender);
                if(message.sender.equals(UtilityFunctions.getUserNameFromFirebase())) {
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    dialog_box.setBackgroundColor(Color.parseColor(colors[messageRecievers.indexOf(message.sender)]));
                }
                else {
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    dialog_box.setBackgroundColor(Color.parseColor(colors[messageRecievers.indexOf(message.sender)]));
                }


                TextView username = v.findViewById(R.id.user_name_dialog);
                username.setText(message.sender);

                TextView user_message = v.findViewById(R.id.dialog);
                user_message.setText(message.message);

                TextView time_display = v.findViewById(R.id.time_display);
                time_display.setText(UtilityFunctions.milliToTime(message.currentTime));

                LinearLayout message_view = findViewById(R.id.message_layout);
                message_view.addView(v);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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


    }

    private void SendMessage(int currentMessageId)
    {
        if(currentMessageId < 0)
            currentMessageId = 0;
        else
            currentMessageId++;


        String message = message_box.getText().toString();
        message_box.setText("");

        if(message.length() > 0)
        {
            for(int i = 0; i < messageRecievers.size(); i++)
            {
                DatabaseReference db = FirebaseDatabase.getInstance().getReference("users/" + messageRecievers.get(i) +"/messages/" + buildAddress(messageRecievers.get(i), messageRecievers)+ "/" + currentMessageId);
                db.setValue(new Message(messageRecievers.get(0).toUpperCase(), message, Calendar.getInstance().getTimeInMillis()));
            }


            if(newMessage)
                ReloadScreenToMessage(buildAddress(messageRecievers.get(0), messageRecievers));

        } else{
            Toast t = Toast.makeText(getApplicationContext(), "You cant send a empty message.", Toast.LENGTH_SHORT);
            t.show();
        }
    }

    public String buildAddress(String user, ArrayList<String> usersList)
    {
        String address = "";
        usersList.remove(user);

        for(int i = 0; i < usersList.size(); i++)
        {
            address += usersList.get(i);

            if(i < usersList.size() -1)
            {
                address += ",";
            }
        }

        return address;
    }

    private void  ReloadScreenToMessage(String message_reference)
    {
        getIntent().putExtra("message_id", message_reference);
        startActivity(getIntent());
    }

    private void getListOfUsers()
    {
       // final ArrayList<UserContactInformation> users = new ArrayList<UserContactInformation>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                UserContactInformation user_info = new UserContactInformation();
                user_info.user_number = dataSnapshot.getKey();
                user_info.username = dataSnapshot.child("username").getValue().toString();

                users.add(user_info);
                Log.e("inside method", String.valueOf(users.size()));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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


        Log.e("user size", String.valueOf( users.size()));
        for(UserContactInformation u : users)
        {
            Log.e("user", u.user_number);
        }
    }


    public class UserContactInformation
    {
        public String username;
        public String user_number;
    }
}


