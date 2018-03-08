package com.itbstudentapp.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itbstudentapp.UtilityFunctions;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by andrew on 04/03/2018.
 */

public class UserSettings
{
    public static boolean play_sounds = true;
    public static boolean vibrate = true;
    public static boolean flash = true;
    public static boolean location = true;

    public static boolean hasChecked = false;
    public static String username;
    public static String studentCourse;
    public static String student_groups[];
    public static String accountType;

    private static void setupPlayerOptions(Context ct)
    {
        SharedPreferences preferences = ct.getSharedPreferences(UtilityFunctions.PREF_FILE, ct.MODE_PRIVATE);

        username = preferences.getString("username", "");
        accountType = preferences.getString("accountType", "");
        studentCourse = preferences.getString("courseID", "");

        String[] groups = preferences.getString("studentGroups", "").split(":");
        student_groups = groups;

        vibrate = preferences.getBoolean("vibrate", true);
        play_sounds = preferences.getBoolean("sound", true);
        flash = preferences.getBoolean("led", true);
        location = preferences.getBoolean("geo", true);
    }






    public static Intent currentIntent;

    public static void checkIfInit(final Context ct, String username)
    {
        final SharedPreferences pref = ct.getSharedPreferences(UtilityFunctions.PREF_FILE, ct.MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();

        if(pref.getBoolean("hasChecked", false))
        {
            if(username == null || username == "")
                setupPlayerOptions(ct);

            return;
        }


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/" + username);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                editor.putBoolean("hasChecked", true);
                editor.putString("username", dataSnapshot.child("username").getValue(String.class).split("@")[0]);
                editor.putString("accountType", dataSnapshot.child("accountType").getValue(String.class));

                if(dataSnapshot.child("accountType").getValue(String.class).equalsIgnoreCase("student"))
                {
                    editor.putString("studentGroups", dataSnapshot.child("groups").getValue(String.class));
                    editor.putString("courseID", dataSnapshot.child("courseID").getValue(String.class));
                }

                editor.apply();
                setupPlayerOptions(ct);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void clearFile(Context ct)
    {
        SharedPreferences.Editor editor = ct.getSharedPreferences(UtilityFunctions.PREF_FILE, ct.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }
}
