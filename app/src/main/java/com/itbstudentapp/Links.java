package com.itbstudentapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Links extends AppCompatActivity implements View.OnClickListener{
    private Button home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_links);

        home = (Button) findViewById(R.id.home);
        home.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }
    public void goToMoodle (View view) {
        goToUrl ( "https://moodle.itb.ie");
    }
    public void goToITB (View view) {
        goToUrl ( "http://www.itb.ie/");
    }

    public void goToEmail (View view) {
        goToUrl ( "https://outlook.office.com/owa/?realm=student.itb.ie");
    }
    public void goToPassReset(View view){
        goToUrl("http://www.itb.ie/CurrentStudents/passwordrecovery.html");
    }

    public void goToITBOneDrive(View view) {
        goToUrl("https://studentitb-my.sharepoint.com");
    }
    public void goToExamInfo(View view) {
        goToUrl("http://www.itb.ie/CurrentStudents/exams.html");
    }
    public void goToPastPapers(View view) {
        goToUrl("http://itbstudenthub.ie/?p=62");
    }
    public void goToITBPortal(View view) {
        goToUrl("https://portal.itb.ie/");
    }

    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }
}
