package com.itbstudentapp;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class NewPostModal extends DialogFragment implements View.OnClickListener {
    private static String path;
    private View v;

    private EditText user_text_box;
    private ImageView imageView;
    private TextView image_name_link;
    private TextView postButton, newImageButton;

    private ProgressDialog progress;

    private static final int request_code = 1;
    private Uri image_upload;

    static NewPostModal newInstance(String dbPath) {
        path = dbPath;

        return new NewPostModal();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.forum_new_post, container, false);

        user_text_box = v.findViewById(R.id.new_post_text_box);
        imageView = v.findViewById(R.id.new_post_image);
        image_name_link = v.findViewById(R.id.new_post_image_link);
        postButton = v.findViewById(R.id.new_post_post);
        newImageButton = v.findViewById(R.id.new_post_image_upload);

        postButton.setOnClickListener(this);
        newImageButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == newImageButton.getId()) {
            uploadImage();
            return;
        }

        if (user_text_box.getText().length() <= 0) {
            Toast.makeText(v.getContext(), "You must enter a message to post.", Toast.LENGTH_SHORT).show();
            return;
        }

        showPostingProgress();

        long currentTime = Calendar.getInstance().getTimeInMillis();

        ForumPost post = new ForumPost(String.valueOf(currentTime), UtilityFunctions.getUserNameFromFirebase(), user_text_box.getText().toString(), currentTime);
        boolean hasPosted = new ForumManager(v.getContext()).addNewPostToDatabase(path, post, image_upload, this);

    }

    public void postMessage()
    {
        progress.dismiss();
        Toast.makeText(v.getContext(), "Topic posted successfully", Toast.LENGTH_SHORT).show();
        this.dismiss();
    }

    private void uploadImage() {
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(Intent.createChooser(gallery, "Pick a file to upload"), request_code);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == request_code) {
            if (resultCode == Activity.RESULT_OK) {
                image_upload = data.getData();
                imageView.setImageURI(image_upload);

                String link[] = image_upload.getPath().split("/");
                image_name_link.setText(link[link.length - 1]);
            }
        }
    }

    private void showPostingProgress() {
        progress = new ProgressDialog(v.getContext());
        progress.setTitle("Posting");
        progress.setMessage("Please wait");
        progress.setCancelable(false);
        progress.show();
    }
}
