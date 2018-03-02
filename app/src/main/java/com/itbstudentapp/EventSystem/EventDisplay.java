package com.itbstudentapp.EventSystem;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itbstudentapp.R;
import com.itbstudentapp.UtilityFunctions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class EventDisplay implements View.OnClickListener {
    private Activity context;
    private TextView eventDisplay;

    final AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
    final AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);

    private boolean fadeAnim = false;
    int currentMessage = 0;
    ArrayList<Event> events = new ArrayList<>();

    Timer t;

    public EventDisplay(Activity context, TextView eventDisplay)
    {
        this.eventDisplay = eventDisplay;
        eventDisplay.setOnClickListener(this);
        this.context = context;


        fadeIn.setDuration(1200);
        fadeIn.setFillAfter(true);
        fadeOut.setDuration(1200);
        fadeOut.setFillAfter(true);

        if(!UtilityFunctions.doesUserHaveConnection(context))
        {
            eventDisplay.setText("No network connection");
            eventDisplay.setVisibility(View.VISIBLE);
            checkForReconnection();
        } else{
            getEvents();
        }
    }

    public void getEvents()
    {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("events");

        events = new ArrayList<>();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot snap : dataSnapshot.getChildren())
                    {
                        Event e = snap.getValue(Event.class);

                        if(e.getEventValidTill() > Calendar.getInstance().getTimeInMillis())
                        {
                            events.add(e);
                        } else{
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
        }, 1000, 10000);
    }

    private void setScroll()
    {

        t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                doThreadAction();
            }
        }, 0, 10000);
    }

    private void doThreadAction()
    {

        context.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                if (!fadeAnim) {
                    //eventDisplay.startAnimation(fadeOut);
                    eventDisplay.setVisibility(View.INVISIBLE);

                    try {
                        eventDisplay.setText(events.get(currentMessage).getEventTitle());
                    } catch (IndexOutOfBoundsException e)
                    {
                        currentMessage = 0;
                        if(currentMessage > 0) {
                            eventDisplay.setText(events.get(currentMessage).getEventTitle());
                        }
                    }

                    fadeAnim = true;
                } else {
                    eventDisplay.startAnimation(fadeIn);
                    fadeAnim = false;

                    if(events.size() == 0)
                    {
                        t.cancel();
                        getEvents();
                        return;
                    }

                    currentMessage = (currentMessage + 1) % events.size();
                }
            }
        });
    }


    @Override
    public void onClick(View v)
    {
        if(v.getId() == eventDisplay.getId())
        {
            Event event = events.get(currentMessage);

            final Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.setContentView(R.layout.event_read_dialog);
            TextView eventTitle = dialog.findViewById(R.id.event_title);
            eventTitle.setText(event.getEventTitle());

            ImageView eventImage = dialog.findViewById(R.id.event_image);
            if(event.getEventImage() == null)
            {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                eventImage.setVisibility(View.INVISIBLE);
                eventImage.setLayoutParams(params);
            } else {
                // get image
            }

            TextView eventDesc = dialog.findViewById(R.id.event_dialog_description);
            eventDesc.setText(event.getEventMessage());

            TextView eventValid = dialog.findViewById(R.id.event_till);
            eventValid.setText(eventValid.getText() + " " + UtilityFunctions.milliToDate(event.getEventValidTill()));

            TextView dismiss = dialog.findViewById(R.id.event_diss);
            dismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
}
