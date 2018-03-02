package com.itbstudentapp.EventSystem;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.itbstudentapp.R;

public class EventsHandler extends AppCompatActivity implements View.OnClickListener {

    private TextView addEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_handler);

        addEvent = findViewById(R.id.add_event);
        addEvent.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == addEvent.getId())
        {
            new EventDialog(this, this);
        }
    }

    private void addNewEventDialog()
    {

    }


}
