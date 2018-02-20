package com.itbstudentapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class UtilityFunctions {

    //Unexpected response code 400 for https://www.googleapis.com/identitytoolkit/v3/relyingparty/verifyPassword?alt=proto&key=AIzaSyB9mqThE6rggW05s69Zb2p82oiIxlnonZg
    // TODO, values not retained. help me

    public static boolean isStaffMember;
    public static String accountType;
    public static String studentCourse = "bn104";
    public static String student_group = "group_1";

    public static String getUserNameFromFirebase()
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String user_name = auth.getCurrentUser().getEmail().toString();
        user_name = user_name.split("@")[0].toLowerCase();

        return  user_name;
    }

    public static String[] getNumberOfColors(int numberOfUsers)
    {
        String[] colors = {"#a5d389","#84ceb7","#dbd64a", "#a5d389","#84ceb7","#dbd64a"};

        String[] returnColors = new String[numberOfUsers];

        for(int i = 0; i < numberOfUsers; i++)
        {
            returnColors[i] = colors[i];
        }

        return returnColors;
    }

    public static String milliToTime(long currentTime)
    {
        Date messageDate = new Date(currentTime);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yy");
        String date = simpleDateFormat.format(messageDate);
        return  date;
    }

    public static boolean doesUserHaveConnection(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo == null)
            return false;

        return networkInfo.isConnected();
    }

    public static String formatForumTitles(String forum_topic)
    {
        String topicArray[] = forum_topic.split("/");
        String topic = topicArray[topicArray.length - 1];

        if(topic.contains("forum")) {
            topic = topic.split("_")[1];
        } else {
            topic = topic.replace("_", " ");
        }

        topic = topic.substring(0,1).toUpperCase() + topic.substring(1, topic.length()).toLowerCase();

        return topic;
    }

    public static Toolbar getApplicationToolbar(final Activity context)
    {
        Toolbar toolbar = (Toolbar) context.findViewById(R.id.tool_bar);

        final ImageView menuButton = toolbar.findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(context.getBaseContext(), menuButton);

                menu.getMenu().add("Profile Settings");
               /*
                if(accountType.equalsIgnoreCase("itb-staff"))
                {
                    menu.getMenu().add("Admin panel");
                }

                if(accountType .equalsIgnoreCase("lecturer") || accountType.equalsIgnoreCase( "itb-staff"))
                {
                    menu.getMenu().add("Set quiz");
                } */

                menu.getMenu().add("Contact us");
                menu.getMenu().add("Logout");

                menu.show();

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        String menuItem = item.getTitle().toString().toLowerCase();

                        switch (menuItem)
                        {
                            case "profile settings":
                                loadMenuIntent(context, ProfileSettings.class);
                                break;
                            case "admin panel":
                                break;
                            case "set quiz":
                                break;
                            case "contact us":
                                break;
                            case "logout":
                                FirebaseAuth auth = FirebaseAuth.getInstance();
                                auth.signOut();
                                Intent intent = new Intent(context, LoginScreen.class);
                                context.startActivity(intent);
                                break;
                        }
                        return true;
                    }
                });
            }
        });

        return toolbar;
    }

    private static void loadMenuIntent(Activity context, Class activityClass)
    {
        Intent intent = new Intent(context, activityClass);
        context.startActivity(intent);
        context.finish();
    }
}
