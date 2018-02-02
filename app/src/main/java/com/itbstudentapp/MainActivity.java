package com.itbstudentapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button forum, transport, map, timetable, quiz, chat, links, phone;
    private Button TEMP_LOG_OUT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, MessageService.class));

        forum = (Button) findViewById(R.id.forum);
        transport =  (Button) findViewById(R.id.transport);
        map = (Button)findViewById(R.id.map);
        timetable = (Button) findViewById(R.id.timetable);
        quiz = (Button)findViewById(R.id.quiz);
        chat = (Button) findViewById(R.id.chat);
        links = (Button) findViewById(R.id.links);
        phone = (Button) findViewById(R.id.phone);
        TEMP_LOG_OUT = (Button) findViewById(R.id.logout);

        phone.setOnClickListener(this);
        transport.setOnClickListener(this);
        map.setOnClickListener(this);
        quiz.setOnClickListener(this);
        chat.setOnClickListener(this);
        links.setOnClickListener(this);
        forum.setOnClickListener(this);
        timetable.setOnClickListener(this);

        TEMP_LOG_OUT.setOnClickListener(this);


    }


    // this interputs the button which clicked
    public void onClick(View view) {

        if(view.getId() == R.id.logout)
        {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();

            Intent intent = new Intent(this, LoginScreen.class);
            startActivity(intent);
        }

        switch (view.getId()){
            case R.id.transport:
                startActivity(new Intent(this, Transport.class));
                break;
            case R.id.forum:
                startActivity(new Intent(this, Forum.class));
                break;
            case R.id.map:
                startActivity(new Intent(this, Map.class));
                break;
            case R.id.quiz:
                startActivity(new Intent(this, Quiz.class));
                break;
            case R.id.links:
                startActivity(new Intent(this, Links.class));
                break;
            case R.id.chat:
                startActivity(new Intent(this, Chat.class));
                break;
            case R.id.phone:
                startActivity(new Intent(this, Phone.class));
                break;
            case R.id.timetable:
                startActivity(new Intent(this, Timetable.class));
                break;

        }

    }


}
