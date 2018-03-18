package com.itbstudentapp.QuizSystem;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itbstudentapp.MainActivity;
import com.itbstudentapp.R;
import com.itbstudentapp.UtilityFunctions;
import com.itbstudentapp.utils.UserSettings;

public class QuizHome extends AppCompatActivity {

    private LinearLayout quizSection; 
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_home);
        setSupportActionBar(UtilityFunctions.getApplicationToolbar(this));
        quizSection = findViewById(R.id.quiz_subjects);
        
        UserSettings.checkIfInit(this, UtilityFunctions.getUserNameFromFirebase());
        

        if (!UtilityFunctions.doesUserHaveConnection(this)) {
            Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }


        loadOptions();
    }

    private void loadOptions() {
        final LinearLayout quizSection = findViewById(R.id.quiz_subjects);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("quiz");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                for (final DataSnapshot quizTopics : dataSnapshot.getChildren()) {

                    View view = LayoutInflater.from(quizSection.getContext()).inflate(R.layout.contact_button, null);
                    LinearLayout layout = view.findViewById(R.id.contact_button);

                    TextView textView = view.findViewById(R.id.contact_text);
                    textView.setText(quizTopics.getKey());

                    layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            quizSection.removeAllViews();
                            loadQuizTopics(quizTopics.getKey());
                        }
                    });

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 0, 20);

                    quizSection.addView(view, params);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadQuizTopics(String key) 
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("quiz/" + key);
        Log.e("huh", "loadQuizTopics: " + reference.getRef() );

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                for (final DataSnapshot quizTitle : dataSnapshot.getChildren()) {
                    if(quizTitle.getKey().equalsIgnoreCase("course_ids") || quizTitle.getKey().equalsIgnoreCase("quiz_master"))
                        continue;

                    View view = LayoutInflater.from(quizSection.getContext()).inflate(R.layout.contact_button, null);
                    LinearLayout layout = view.findViewById(R.id.contact_button);

                    TextView textView = view.findViewById(R.id.contact_text);
                    textView.setText(quizTitle.getKey());

                    layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startQuiz(dataSnapshot.getKey() +"/" + quizTitle.getKey());
                        }
                    });

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 0, 20);

                    quizSection.addView(view, params);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void startQuiz(String key)
    {
        Intent intent = new Intent(this, Quiz.class);
        intent.putExtra("quiz", key);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}


/*


package com.itbstudentapp;



import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


public class Links extends AppCompatActivity {

    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_links);
        String[] contacts = getResources().getStringArray(R.array.links);

        linearLayout = findViewById(R.id.link_grid);

        for(int i = 0; i < contacts.length; i++) {
            String temp[] = contacts[i].split("_");
            String name = temp[0];
            final String link = temp[1];

            View view = LayoutInflater.from(this).inflate(R.layout.contact_button, null);
            LinearLayout layout = view.findViewById(R.id.contact_button);
            layout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#cc" + getHexColor(i))));

            TextView textView = view.findViewById(R.id.contact_text);
            textView.setText(name);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uriUrl = Uri.parse(link);
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                    startActivity(launchBrowser);
                }
            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 20);

            linearLayout.addView(view, i, params);
        }

    }

    private String getHexColor(int index)
    {
        String[] colorHexes = getResources().getStringArray(R.array.colours);
        return colorHexes[index % colorHexes.length];
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }

}

* */