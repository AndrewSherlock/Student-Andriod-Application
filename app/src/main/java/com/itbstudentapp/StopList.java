package com.itbstudentapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class StopList extends AppCompatActivity {

    private String route;
    private Stop stops[];
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_list);
        linearLayout = (LinearLayout) findViewById(R.id.bus_stop_list);

        route = getIntent().getStringExtra("route"); // get route from the last intent

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                StopInformationFinder si = new StopInformationFinder(); // get stops from resource class
                stops = si.doInBackground(route); // async class that does work in background
            }
        });
        th.start();

        try {
            th.join(); // we need the thread to wait before starting the next action
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(Stop s : stops)
        {
            DrawButttons(s); // draws buttons
        }

    }

    private String choosenStop = null;
    private void DrawButttons(Stop st)
    {
        final Button stopButton = new Button(this);
        stopButton.setText(st.getStop_name());
        stopButton.setTag(st.getStop_number() + ":" + st.getStop_name());

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info[] = v.getTag().toString().split(":"); // because we want to hold 2 bits of info in tag
                choosenStop = info[1]; // to bundle
                GetNextIntent(info[0]); // start next intenet

            }
        });

        LinearLayout.LayoutParams linear = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        // we need to change this for something more stylish.
        ((LinearLayout)linearLayout).addView(stopButton); // add to view
    }

    private void GetNextIntent(String stopNum)
    {
        Intent intent = new Intent(this, BusTimes.class); // create new intent
        intent.putExtra("route", this.route); // bundle resources
        intent.putExtra("stop", stopNum);
        intent.putExtra("stop_name", choosenStop);
        startActivity(intent); // start
    }
}
