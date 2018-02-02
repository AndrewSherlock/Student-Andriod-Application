package com.itbstudentapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;

public class ForumList extends AppCompatActivity {

    private int numOfTopics = 0;
    private ForumList instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_topic_list);
        instance = this;

        Bundle b = getIntent().getExtras();
        final String path = b.getString("path") + b.getInt("index") + "/topics";

        ForumManager forumManager = new ForumManager(path);
        forumManager.addPostToView((LinearLayout) findViewById(R.id.forum_display),path); // gets the topics within database section and adds to view

        Button newMessage = findViewById(R.id.forum_new_message_button);
        newMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NewForumPost(v.getContext(), path, instance);
            }
        });

    }


    //TODO this should not be here, rethink this

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("image_ok", String.valueOf(requestCode));
        if(resultCode == RESULT_OK)
        {
            Uri uri = data.getData();
            ImageUploader imageUploader = new ImageUploader();
            imageUploader.ImageUpload(this, uri);
        }
    }
}
