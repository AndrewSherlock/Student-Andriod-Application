package com.itbstudentapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.itbstudentapp.ChatSystem.Chat;
import com.itbstudentapp.EventSystem.EventDisplay;
import com.itbstudentapp.QuizSystem.QuizHome;
import com.itbstudentapp.utils.UserSettings;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout forum, transport, map, timetable, quiz, chat, links, phone;
    private Button TEMP_LOG_OUT;

    private EventDisplay eventDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UserSettings.currentIntent = this.getIntent();

        SharedPreferences editor = getSharedPreferences(UtilityFunctions.PREF_FILE, MODE_PRIVATE);
        UserSettings.checkIfInit(this, UtilityFunctions.getUserNameFromFirebase());

        Log.e("here", "onCreate: " + editor.getBoolean("vibrate", true));

        Log.e("username", "onCreate: " + editor.getString("username", "unknown"));
        Log.e("accountType", "onCreate: " + editor.getString("accountType", "unknown"));
        Log.e("course", "onCreate: " + editor.getString("courseID", "unknown"));
        Log.e("course", "onCreate: " + editor.getString("studentGroups", "unknown"));

        setSupportActionBar(UtilityFunctions.getApplicationToolbar(this));

        forum =  findViewById(R.id.forum);
        transport = findViewById(R.id.transport);
        map = findViewById(R.id.map);
        timetable = findViewById(R.id.timetable);
        quiz = findViewById(R.id.quiz);
        chat = findViewById(R.id.chat);
        links = findViewById(R.id.links);
        phone = findViewById(R.id.phone);

        phone.setOnClickListener(this);
        transport.setOnClickListener(this);
        map.setOnClickListener(this);
        quiz.setOnClickListener(this);
        chat.setOnClickListener(this);
        links.setOnClickListener(this);
        forum.setOnClickListener(this);
        timetable.setOnClickListener(this);

//        TEMP_LOG_OUT.setOnClickListener(this);
        eventDisplay = new EventDisplay(this, (TextView) findViewById(R.id.event_message));


    }

    // this interputs the button which clicked
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.transport:
                startActivity(new Intent(this, Transport.class));
                onActivityChange();
                finish();
                break;
            case R.id.forum:
                startActivity(new Intent(this, Forum.class));
                onActivityChange();
                finish();
                break;
            case R.id.map:
                startActivity(new Intent(this, MapActivity.class));
                onActivityChange();
                finish();
                break;
            case R.id.quiz:
                //startActivity(new Intent(this, Quiz.class));
                startActivity(new Intent(this, QuizHome.class));
                onActivityChange();
                finish();
                break;
            case R.id.links:
                startActivity(new Intent(this, Links.class));
                onActivityChange();
                finish();
                break;
            case R.id.chat:
                startActivity(new Intent(this, Chat.class));
                onActivityChange();
                finish();
                break;
            case R.id.phone:
                startActivity(new Intent(this, Phone.class));
                onActivityChange();
                finish();
                break;
            case R.id.timetable:
                startActivity(new Intent(this, Timetable.class));
                onActivityChange();
                finish();
                break;

        }

    }

    private void onActivityChange()
    {
        if(eventDisplay.displayHandler != null)
            eventDisplay.displayHandler.removeCallbacksAndMessages(null);
    }
}
