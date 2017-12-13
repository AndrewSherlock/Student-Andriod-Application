package com.itbstudentapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class BusTimes extends AppCompatActivity {

    private  BusTimeInfo[] times;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_times);

        final String route = getIntent().getStringExtra("route");
        final String stop = getIntent().getStringExtra("stop");
        final String stop_name = getIntent().getStringExtra("stop_name");

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                 BusTimeReciever btr = new BusTimeReciever(); // gets the times from the server
                 times = btr.doInBackground(route, stop); // async class
            }
        });

        th.start();

        try{
            th.join(); // need to wait
        } catch (InterruptedException e){}

        populateList(route, stop, stop_name, times); // method that populates the screen

    }

    private void populateList(String route, String stop, String stop_name, BusTimeInfo times[])
    {
        LinearLayout layout = findViewById(R.id.times_list); // get the layout
        Log.e("here", "populateList: " + times.length );

        if(times == null || times.length == 0) {
            Toast.makeText(getApplicationContext(), "No buses currently listed", Toast.LENGTH_LONG).show();
            return;
        }
        for(BusTimeInfo time : times) // for each loop
        {
            View v = LayoutInflater.from(this).inflate(R.layout.bus_time_display, layout, false); // new instance of view

            TextView time_text = (TextView) v.findViewById(R.id.bus_time_text); // get textviews in that view
            TextView dest_text = (TextView) v.findViewById(R.id.dest_text);
            TextView bus_src = (TextView) v.findViewById(R.id.bus_src);

            time_text.setText(time.getBus_time() + " Mins");
            dest_text.setText(route + " " + time.getBus_dest()); // destination
            bus_src.setText("via " + stop_name);


            ((LinearLayout) layout).addView(v); // add to the view
        }
    }
}
