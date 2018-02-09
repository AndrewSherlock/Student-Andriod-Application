package com.itbstudentapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ForumList extends AppCompatActivity {

    private int numOfTopics = 0;
    private ForumList instance;
    private ImageController imageController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_topic_list);
        instance = this;
        imageController = new ImageController(this);

        Bundle b = getIntent().getExtras();
        final String path = b.getString("path") + b.getInt("index") + "/topics";

        ForumManager forumManager = new ForumManager(path);
        forumManager.addPostToView((LinearLayout) findViewById(R.id.forum_display),path); // gets the topics within database section and adds to view

        TextView forum_title = findViewById(R.id.forum_title);
        forum_title.setText(b.getString("title"));

        Button newMessage = findViewById(R.id.forum_new_message_button);
        newMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NewForumPost(v.getContext(), path, instance, imageController);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK)
        {
            Uri uri = data.getData();
            imageController.setUploadUri(uri);
        }
    }
}