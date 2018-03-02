package com.itbstudentapp.EventSystem;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itbstudentapp.UtilityFunctions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class EventDisplay implements View.OnClickListener {
    private Activity context;
    private TextView eventDisplay, addEvent;

    private boolean fadeAnim = false;
    int currentMessage = 0;
    ArrayList<Event> events = new ArrayList<>();

    public EventDisplay(Activity context, TextView eventDisplay, TextView addEvent)
    {
        this.eventDisplay = eventDisplay;
        this.addEvent = addEvent;
        eventDisplay.setOnClickListener(this);
        addEvent.setOnClickListener(this);
        this.context = context;

        if(!UtilityFunctions.doesUserHaveConnection(context))
        {
            eventDisplay.setText("No network connection");
            eventDisplay.setVisibility(View.VISIBLE);
            checkForReconnection();
        } else{
            getEvents();
        }
    }

    private void getEvents()
    {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("events");

        if(events == null)
            events = new ArrayList<>();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot snap : dataSnapshot.getChildren())
                    {
                        Event e = snap.getValue(Event.class);
                        Log.e("jeje", "onDataChange: " + e.getEventValidTill() + "/"  +  Calendar.getInstance().getTimeInMillis());

                        if(e.getEventValidTill() > Calendar.getInstance().getTimeInMillis())
                        {
                            events.add(e);
                        } else{
                            Log.e("jeje", "onDataChange: " + reference.child(dataSnapshot.getKey()) );
                            reference.child(dataSnapshot.getKey()).getRef().removeValue();
                        }

                    }

                    if(events.size() > 0)
                        setScroll();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkForReconnection()
    {
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {

                if(UtilityFunctions.doesUserHaveConnection(context))
                {
                    cancel();
                    getEvents();
                }
            }
        }, 0, 5000);
    }

    private void setScroll()
    {

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                doThreadAction();
            }
        }, 0, 4000);
    }

    private void doThreadAction()
    {
        final AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        final AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);

        fadeIn.setDuration(1200);
        fadeIn.setFillAfter(true);
        fadeOut.setDuration(1200);
        fadeOut.setFillAfter(true);


        context.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                if (!fadeAnim) {
                    eventDisplay.setText(events.get(currentMessage).getEventTitle());
                    eventDisplay.startAnimation(fadeIn);
                    fadeAnim = true;
                } else {
                    eventDisplay.startAnimation(fadeOut);
                    fadeAnim = false;
                    currentMessage = (currentMessage + 1) % events.size();
                }
            }
        });
    }


    @Override
    public void onClick(View v)
    {
        if(v.getId() == addEvent.getId())
        {
            Event e = new Event("Message test", "I am a event", Calendar.getInstance().getTimeInMillis(),
                    Calendar.getInstance().getTimeInMillis() + 10000, null);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("events");
            reference.child("Event_test").setValue(e);
        }

        if(v.getId() == eventDisplay.getId())
        {
            Dialog dialog = new Dialog(context, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
            dialog.show();
        }

    }
}
