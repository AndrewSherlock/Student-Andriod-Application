package com.itbstudentapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.UUID;

public class NewForumPost  extends AppCompatActivity {

    private Dialog dialog;
    private ImageController imageController;
    private Activity callingActivity;

    public NewForumPost(final Context context, final String path, Activity callingActivity, ImageController imageController){

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.forum_new_post);
        this.callingActivity = callingActivity;

        Button new_post = dialog.findViewById(R.id.modal_new_post_post);
        new_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewPostToDatabase(path);
            }
        });

        Button image = dialog.findViewById(R.id.modal_new_post_add_image);

        this.imageController = imageController;
        image.setOnClickListener(imageController);

        dialog.show();

    }

    public void addNewPostToDatabase(String path)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(path);

        EditText user_message = dialog.findViewById(R.id.forum_new_post_text);
        String user_text = user_message.getText().toString();

        if(user_text.length() > 0) {
            ForumPost post;

            if(!UtilityFunctions.doesUserHaveConnection(callingActivity))
            {
                Toast.makeText(callingActivity, "No network connection. Please try again", Toast.LENGTH_SHORT).show();
                return;
            }

            if(imageController.getUploadedUri() != null)
            {
                imageController.ImageUpload(callingActivity, imageController.getUploadedUri());
                post = new ForumPost(UUID.randomUUID().toString(), UtilityFunctions.getUserNameFromFirebase(),
                        user_text, Calendar.getInstance().getTimeInMillis(), imageController.getFileId());
            } else {
                post = new ForumPost(UUID.randomUUID().toString(), UtilityFunctions.getUserNameFromFirebase(),
                        user_text, Calendar.getInstance().getTimeInMillis());
            }

            reference.child(post.getPostTitle()).setValue(post);
            dialog.dismiss();

        } else{
            Toast.makeText(dialog.getContext(), "You must enter text to post", Toast.LENGTH_SHORT).show();
            return;
        }

    }
}
