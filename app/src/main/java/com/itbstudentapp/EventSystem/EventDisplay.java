package com.itbstudentapp.EventSystem;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
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

    private final AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
    private final AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);

    private long lastCall =0;
    private int currentMessage = 0;
    private ArrayList<Event> events = new ArrayList<>();

    public Handler displayHandler;

    public EventDisplay(Activity context, TextView eventDisplay)
    {
        this.eventDisplay = eventDisplay;
        eventDisplay.setOnClickListener(this);
        this.context = context;


        fadeIn.setDuration(1200);
        fadeIn.setFillAfter(true);
        fadeOut.setDuration(1200);
        fadeOut.setFillAfter(true);

        lastCall = Calendar.getInstance().getTimeInMillis();

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
                            reference.child(dataSnapshot.getKey()).getRef().setValue(null);
                        }
                    }

                    if(events.size() > 0)
                        displayAction(100);
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

    private void displayAction(final int time)
    {
        displayHandler = new android.os.Handler();
        displayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean firstTurn = (time < 1000);
                doThreadAction(firstTurn);
            }
        }, time);


    }


    private void doThreadAction(boolean firstTurn)
    {
        if((Calendar.getInstance().getTimeInMillis()-lastCall) < 9000 && !firstTurn)
            return;

        lastCall = Calendar.getInstance().getTimeInMillis();
        context.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                currentMessage = (currentMessage + 1) % events.size();
                eventDisplay.setVisibility(View.INVISIBLE);
                eventDisplay.setText(events.get(currentMessage).getEventTitle());
                eventDisplay.startAnimation(fadeIn);
                displayAction(10000);
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
                UtilityFunctions.loadEventImageToView(event.getEventImage(), context,  eventImage);
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
