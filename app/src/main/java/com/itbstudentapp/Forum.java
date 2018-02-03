package com.itbstudentapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Forum extends AppCompatActivity implements View.OnClickListener{


    private DatabaseReference ref;
    private Forum instance;
    private LinearLayout layout;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        Bundle b = getIntent().getExtras();
        path = "forum/sections/";

        if(b != null)
        {
            if(b.getString("sectionChoice").equals("0"))
            {
                path = "forum/sections/0/modules";
            }
        } else{
            path = "forum/sections/";
        }

        instance = this;
        ref = FirebaseDatabase.getInstance().getReference(path);
        layout = findViewById(R.id.sections);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                View v = LayoutInflater.from(instance).inflate(R.layout.forum_button, null);
                ForumSection fs = dataSnapshot.getValue(ForumSection.class);
                RelativeLayout rel = v.findViewById(R.id.section_button);

                TextView section_title = v.findViewById(R.id.section_name);
                section_title.setText(fs.getSectionName());

                TextView section_desc = v.findViewById(R.id.section_desc);
                section_desc.setText(fs.getSectionDesc());

                rel.setOnClickListener(instance);

                v.setId(Integer.parseInt(dataSnapshot.getKey()));
                instance.layout.addView(v);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }


    @Override
    public void onClick(View v) {

        String link = "";

//        final String path = b.getString("path") + b.getInt("index") + "/topics";


        switch (v.getId()) // can be removed
        {
            case 0:
                Intent thisIntent = getIntent();
                thisIntent.putExtra("sectionChoice", "0");
                startActivity(thisIntent);
                finish();
                break;
            case 1:  LoadForumForTopic(1, path, "Campus");
                break;
            case 2: LoadForumForTopic(2, path, "Area");
                break;
            case 3: LoadForumForTopic(3, path, "Transport");
                break;
            case 4: LoadForumForTopic(4, path, "Relax");
                break;
                default: Log.e("Error", "Probelms");
        }
    }

    private void LoadForumForTopic(int index, String path, String title)
    {
        Intent intent = new Intent(this, ForumList.class);
        intent.putExtra("index", index);
        intent.putExtra("path", path);
        intent.putExtra("title", title);

        startActivity(intent);
        finish();
    }
}


