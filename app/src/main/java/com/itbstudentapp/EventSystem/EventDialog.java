package com.itbstudentapp.EventSystem;

import android.app.Dialog;
import android.app.Notification;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itbstudentapp.ImageController;
import com.itbstudentapp.Interfaces.OnImageUploaded;
import com.itbstudentapp.NotificationSystem.FirebaseNotificationManager;
import com.itbstudentapp.R;
import com.itbstudentapp.UtilityFunctions;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by andrew on 02/03/2018.
 */

public class EventDialog extends Dialog implements View.OnClickListener, CalendarView.OnDateChangeListener, OnImageUploaded {

    private EventsHandler eventsHandler;

    private EditText eventTitle, eventDesc;
    private TextView eventImageDesc, eventImage, eventAdd, eventDate;

    private CalendarView calendarView;
    private Date choosenDate;
    private ImageController ic;

    private Uri image;
    private String imageLink = null;

    private boolean isEditing = false;
    private Event editEvent;
    private String eventID;

    public EventDialog(@NonNull Context context, EventsHandler activity) {
        super(context, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        this.eventsHandler = activity;
        setContentView(R.layout.event_dialog);

        calendarView = findViewById(R.id.calender_view);
        calendarView.setOnDateChangeListener(this);

        eventTitle = findViewById(R.id.event_title);
        eventDate = findViewById(R.id.event_valid_till);
        eventDesc = findViewById(R.id.event_description);

        eventImageDesc = findViewById(R.id.event_image_dialog);
        eventImage = findViewById(R.id.event_upload_image);
        eventAdd = findViewById(R.id.event_add);

        eventDate.setOnClickListener(this);
        eventAdd.setOnClickListener(this);
        eventImage.setOnClickListener(this);

        show();
    }

    public EventDialog(Context context, EventsHandler eventsHandler, Event event, String event_id) {
        this(context, eventsHandler);

        eventTitle.setText(event.getEventTitle());
        eventDate.setText(UtilityFunctions.milliToDate(event.getEventValidTill()));
        calendarView.setDate(event.getEventValidTill());
        eventDesc.setText(event.getEventMessage());
        eventImageDesc.setText(event.getEventImage());

        eventAdd.setText("Edit");

        editEvent = event;
        this.eventID = event_id;
        isEditing = true;

        choosenDate = new Date(event.getEventValidTill());
        eventsHandler.setEventDialog(this);
    }

    public void setUri(Uri image) {
        this.image = image;
        eventImageDesc.setText(image.getPath());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == eventAdd.getId()) {
            if (image != null) {
                ic.ImageUpload(eventsHandler, image, "events");
                return;
            }

            if (!isEditing)
                uploadEvent();
            else
                editEvent();
        }

        if (v.getId() == eventDate.getId()) {
            showCalender();
        }

        if (v.getId() == eventImage.getId()) {
            ic = new ImageController(eventsHandler, this);
            ic.onClick(v);
        }

    }

    private void editEvent() {
        if (!UtilityFunctions.doesUserHaveConnection(eventsHandler)) {
            Toast.makeText(eventsHandler.getApplicationContext(), "No network connection. Please try again", Toast.LENGTH_LONG).show();
            return;
        }

        String title = eventTitle.getText().toString();
        String eventDescription = eventDesc.getText().toString();
        String date = eventDate.getText().toString();

        if (title.length() < 0 && eventDescription.length() < 0 && date.length() < 0) {
            Toast.makeText(eventsHandler.getApplicationContext(), "Ensure all fields are filled out.", Toast.LENGTH_LONG).show();
            return;
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("events/" + eventID);

        if (!title.equalsIgnoreCase(editEvent.getEventTitle())) {
            reference.child("eventTitle").setValue(title);
        }

        if (!eventDescription.equalsIgnoreCase(editEvent.getEventMessage())) {
            reference.child("eventMessage").setValue(eventDescription);
        }

        Calendar c = Calendar.getInstance();
        c.setTime(choosenDate);
        long dateMilli = c.getTimeInMillis();

        if (dateMilli != editEvent.getEventValidTill()) {
            reference.child("eventValidTill").setValue(dateMilli);
        }

        if (!eventImageDesc.getText().toString().equals(editEvent.getEventImage())) {
            if (image != null) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference("events/" + editEvent.getEventImage());
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });

                ic.ImageUpload(eventsHandler, image, "events");
            }
        }

        Toast.makeText(getContext(), "Event edited.", Toast.LENGTH_SHORT).show();
        dismiss();
    }

    private void showCalender() {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        calendarView.setLayoutParams(params);

        calendarView.setVisibility(View.VISIBLE);
    }

    private void uploadEvent() {
        if (!UtilityFunctions.doesUserHaveConnection(eventsHandler)) {
            Toast.makeText(eventsHandler.getApplicationContext(), "No network connection. Please try again", Toast.LENGTH_LONG).show();
            return;
        }

        String title = eventTitle.getText().toString();
        String eventDescription = eventDesc.getText().toString();
        String date = eventDate.getText().toString();

        if (title.length() < 0 && eventDescription.length() < 0 && date.length() < 0) {
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

        //String notificationType, String title, String body
        com.itbstudentapp.NotificationSystem.Notification notification =
                new com.itbstudentapp.NotificationSystem.Notification("event", "New event posted", title);

       // FirebaseNotificationManager.sendNotificationToUser(notification);


        dismiss();
        eventsHandler.reloadIntent();
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
        if (choosenDate == null)
            choosenDate = new Date();

        choosenDate.setYear(year - 1900);
        choosenDate.setMonth(month);
        choosenDate.setDate(dayOfMonth);

        eventDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);

    }

    @Override
    public void onImageUploaded(String file) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("events/" + eventID);

        if (isEditing) {
            reference.child("eventImage").setValue(file);
            return;
        }

        imageLink = file;
        uploadEvent();
    }
}
