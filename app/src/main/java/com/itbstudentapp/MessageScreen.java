package com.itbstudentapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/*
*  TODO: Multi user texts arnt working as of yet
*  TODO: Notifications arnt working,
*  TODO: When a user has no texts, tell them
*
* */

public class MessageScreen extends AppCompatActivity implements View.OnClickListener{

    private TextView reciepentList;
    private Button contactListButton, imageChooserButton, sendButton;
    private EditText userInputField;

    private ChatController chatController;
    private boolean isNewMessage = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_screen);
        Bundle b = getIntent().getExtras();
        isNewMessage = (b.getString("message_id").equalsIgnoreCase("none"));

        reciepentList = findViewById(R.id.message_screen_reciever_list);
        contactListButton = findViewById(R.id.message_screen_contact);
        imageChooserButton = findViewById(R.id.message_screen_image_upload);
        sendButton = findViewById(R.id.message_screen_send);

        contactListButton.setOnClickListener(this);
        imageChooserButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);

        userInputField = findViewById(R.id.message_screen_text_box);

        chatController = new ChatController(this);

        if(isNewMessage)
            chatController.setUpNewMessage();
        else
            chatController.loadMessages(b.getString("message_id"), (LinearLayout) findViewById(R.id.messages_screen_messages), (ScrollView) findViewById(R.id.chat_scroll_view));

    }

    @Override
    public void onClick(View v) {

        if(v.getId() == contactListButton.getId())
        {
            chatController.chooseUsersFromList(reciepentList);
        }

        if(v.getId() == sendButton.getId())
        {
            if(chatController.hasRecievers())
                chatController.beginMessageSending(userInputField);
            else
                Toast.makeText(this, "You must specify someone to send the message to.", Toast.LENGTH_SHORT).show();
        }

    }
}
