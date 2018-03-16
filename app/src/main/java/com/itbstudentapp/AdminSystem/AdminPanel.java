package com.itbstudentapp.AdminSystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.itbstudentapp.MainActivity;
import com.itbstudentapp.R;

public class AdminPanel extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel2);

        setupButtons();
    }

    private void setupButtons()
    {
        TextView admin_mentor = findViewById(R.id.admin_mentor_panel);
        admin_mentor.setOnClickListener(this);

        TextView admin_mod = findViewById(R.id.admin_moderator);
        admin_mod.setOnClickListener(this);

        TextView admin_quiz = findViewById(R.id.admin_quiz_panel);
        admin_quiz.setOnClickListener(this);

        TextView admin_report = findViewById(R.id.admin_forum_reports);
        admin_report.setOnClickListener(this);

        TextView home_button = findViewById(R.id.admin_home);
        home_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.admin_home)
        {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else if(v.getId() == R.id.admin_mentor_panel)
        {
            showMentorDialog();
        } else if(v.getId() == R.id.admin_moderator)
        {

        } else if(v.getId() == R.id.admin_quiz_panel)
        {

        } else if(v.getId() == R.id.admin_forum_reports)
        {

        }

    }

    private void showMentorDialog() {
    }

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
