package com.itbstudentapp.TrainHandler;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itbstudentapp.MainActivity;
import com.itbstudentapp.Interfaces.OnThreadComplete;
import com.itbstudentapp.R;
import com.itbstudentapp.Transport;
import com.itbstudentapp.UtilityFunctions;

import java.util.ArrayList;

public class TrainTimeTable extends AppCompatActivity implements OnThreadComplete{

    private TrainInformationHandler trainHandler;
    private Thread thread;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_time_table);
        setSupportActionBar(UtilityFunctions.getApplicationToolbar(this));
        trainHandler = new TrainInformationHandler(this);

        if(!UtilityFunctions.doesUserHaveConnection(this))
        {
            Toast.makeText(this, "No network connection. Please try again", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading");
        progressDialog.setTitle("Retrieving times");
        progressDialog.show();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                trainHandler.execute();
            }
        });

        thread.start();
    }

    @Override
    public void onThreadCompleteCall()
    {
        setUpPage();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setUpPage()
    {
        ArrayList<Train> trainInfo = trainHandler.getTrainInfo();
        LinearLayout timesLayout = findViewById(R.id.times_list);

        int counter = 0;

        for(Train t : trainInfo)
        {
            View v = LayoutInflater.from(this).inflate(R.layout.train_time_display, null);
            TextView destination = v.findViewById(R.id.train_route);
            destination.setText(t.getTrainCode() + " " + t.getDestination());
            v.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#cc" + UtilityFunctions.getHexColor(counter++))));

            TextView direction = v.findViewById(R.id.train_direction);
            direction.setText(t.getDirection());

            TextView duein = v.findViewById(R.id.train_due);
            duein.setText(t.getDuein() + " Mins");

            TextView late = v.findViewById(R.id.train_late);
            late.setText("Late : " + t.getLate() + " Mins");

            TextView status = v.findViewById(R.id.train_status);
            status.setText(t.getStatus());

            ImageView warning = v.findViewById(R.id.warning_image);

            if((!t.getStatus().equalsIgnoreCase("En Route") || !t.getStatus().equalsIgnoreCase("no information")) && t.getLate() != 0)
            {
                warning.setVisibility(View.VISIBLE);
            } else {
                warning.setVisibility(View.INVISIBLE);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );
            params.setMargins(0, 0, 0, 25);

            timesLayout.addView(v, params);
        }

        progressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        startActivity(new Intent(this, Transport.class));
        finish();
    }
}
