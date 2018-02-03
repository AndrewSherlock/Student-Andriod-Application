package com.itbstudentapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class UtilityFunctions {

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

        return networkInfo.isConnectedOrConnecting() && networkInfo != null;
    }
}
