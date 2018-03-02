package com.itbstudentapp.EventSystem;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.itbstudentapp.ImageController;
import com.itbstudentapp.Interfaces.OnImageUploaded;
import com.itbstudentapp.R;
import com.itbstudentapp.UtilityFunctions;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by andrew on 02/03/2018.
 */

public class EventDialog extends Dialog implements View.OnClickListener, CalendarView.OnDateChangeListener, OnImageUploaded{

    private EventsHandler eventsHandler;

    private EditText eventTitle, eventDesc;
    private TextView eventImageDesc, eventImage, eventAdd, eventDate;

    private CalendarView calendarView;
    private Date choosenDate;
    private ImageController ic;

    private Uri image;
    private String imageLink = null;

    public EventDialog(@NonNull Context context, EventsHandler activity)
    {
        super(context,  android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        this.eventsHandler = activity;
        setContentView(R.layout.event_dialog);

        calendarView = findViewById(R.id.calender_view);
        calendarView.setOnDateChangeListener(this);

        eventTitle = findViewById(R.id.event_title);
        eventDate = findViewById(R.id.event_valid_till);
        eventDesc = findViewById(R.id.event_description);

        eventImageDesc = findViewById(R.id.event_image_dialog);
        eventImage = findViewById(R.id.event_image_dialog);
        eventAdd = findViewById(R.id.event_add);

        eventDate.setOnClickListener(this);
        eventAdd.setOnClickListener(this);
        eventImage.setOnClickListener(this);

        show();
    }

    public void setUri(Uri image)
    {
        this.image = image;
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == eventAdd.getId())
        {
            if(image != null)
            {
                ic.ImageUpload(eventsHandler, image);
                return;
            }
            uploadEvent();
        }

        if(v.getId() == eventDate.getId())
        {
            showCalender();
        }

        if(v.getId() == eventImage.getId())
        {
            ic = new ImageController(eventsHandler, this);
            ic.onClick(v);
        }

    }

    private void showCalender()
    {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        calendarView.setLayoutParams(params);

        calendarView.setVisibility(View.VISIBLE);
    }

    private void uploadEvent()
    {
        if(!UtilityFunctions.doesUserHaveConnection(eventsHandler))
        {
            Toast.makeText(eventsHandler.getApplicationContext(), "No network connection. Please try again", Toast.LENGTH_LONG).show();
            return;
        }

        String title = eventTitle.getText().toString();
        String eventDescription = eventDesc.getText().toString();
        String date = eventDate.getText().toString();

        if(title.length() < 0 && eventDescription.length() < 0 && date.length() < 0)
        {
            Toast.makeText(eventsHandler.getApplicationContext(), "Ensure all fields are filled out.", Toast.LENGTH_LONG).show();
            return;
        }

        Calendar c = Calendar.getInstance();

        long currentTime = c.getTimeInMillis();

        c.setTime(choosenDate);
        long getValidTill = c.getTimeInMillis();

        Event event = new Event(title, eventDescription, currentTime, getValidTill, imageLink);
        String event_id = UUID.randomUUID().toString();
        FirebaseDatabase.getInstance().getReference("events").child(event_id).setValue(event);

        dismiss();
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
    {
        if(choosenDate == null)
            choosenDate = new Date();

        choosenDate.setYear(year - 1900);
        choosenDate.setMonth(month);
        choosenDate.setDate(dayOfMonth);

        eventDate.setText(dayOfMonth + "/" + month + "/" + year);

    }

    @Override
    public void onImageUploaded(String file)
    {
        imageLink = file;
        uploadEvent();
    }
}
