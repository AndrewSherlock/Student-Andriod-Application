package com.itbstudentapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class NewForumPost  extends AppCompatActivity {

    private Dialog dialog;
    private Image image;
    private static  final int request_code = 1;
    private Activity callingActivitie;

    public NewForumPost(final Context context, final String path, Activity callingActivity){

        this.callingActivitie = callingActivity;

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.forum_new_post);

        Button new_post = dialog.findViewById(R.id.modal_new_post_post);
        new_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewPostToDatabase(path);
            }
        });

        Button image = dialog.findViewById(R.id.modal_new_post_add_image);
        image.setOnClickListener(new ImageUploader(callingActivity));


        //TODO implement image uploading

        dialog.show();

    }

    public void addNewPostToDatabase(String path)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(path);

        EditText user_message = dialog.findViewById(R.id.forum_new_post_text);
        String user_text = user_message.getText().toString();

        if(user_text.length() > 0) {
            ForumPost post = new ForumPost("there is no way to generate id yet", UtilityFunctions.getUserNameFromFirebase(), user_text, Calendar.getInstance().getTimeInMillis());
            reference.child(post.getPostTitle()).setValue(post);
        } else{
            Toast.makeText(dialog.getContext(), "You must enter text to post", Toast.LENGTH_SHORT).show();
            return;
        }

    }
}
