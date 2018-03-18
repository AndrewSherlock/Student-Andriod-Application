package com.itbstudentapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ForumList extends AppCompatActivity implements View.OnClickListener{

    private TextView post_button;
    private TextView forum_title;
    private ForumManager forumManager;
    private String forum_topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_list);
        setSupportActionBar(UtilityFunctions.getApplicationToolbar(this));
        forum_topic = getIntent().getStringExtra("path");

        forum_title = findViewById(R.id.forum_banner_title);
        forum_title.setText(UtilityFunctions.formatTitles(forum_topic));

        post_button = findViewById(R.id.forum_new_post);

        post_button.setOnClickListener(this);
        forumManager = new ForumManager(this);

        forumManager.listenForNewTopics(forum_topic, (LinearLayout) this.findViewById(R.id.forum_post_section));
        forumManager.giveFragmentManager(getFragmentManager());
    }

    @Override
    public void onClick(View v)
    {
        forumManager.addNewUserPost(forum_topic);
    }
/*
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode == RESULT_OK)
        {
//TODO USED ??? HAS TO BE
        }
    } */
}
