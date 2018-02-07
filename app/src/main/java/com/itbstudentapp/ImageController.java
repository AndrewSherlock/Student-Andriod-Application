package com.itbstudentapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.util.UUID;

public class ImageController implements View.OnClickListener {

    private Activity context;
    private static final int request_code = 1;
    private Uri uploadFile = null;
    private String fileId = null;

    public ImageController() {
    }

    public ImageController(Activity activity) {
        context = activity; // we need to do this to use startActivityForResult
    }

    public void setUploadUri(Uri file)
    {
        this.uploadFile = file;
    }

    @Override
    public void onClick(View v) {
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        context.startActivityForResult(Intent.createChooser(gallery, "Pick a file to upload"), request_code);
    }

    public void ImageUpload(final Context context, Uri filePath) {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            fileId = UUID.randomUUID().toString();

            StorageReference ref = FirebaseStorage.getInstance().getReference().child("forumImages/" + fileId);
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(context, "File uploaded", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(context, "File uploaded", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage((int) progress + "% complete.");
                }
            });
        }
    }

    public Uri getUploadedUri() {

        return this.uploadFile;
    }

    public String getFileId()
    {
        return this.fileId;
    }

    public void setImageInView(final ImageView imageView, final String imageLink)
    {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(imageLink);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(imageView.getContext()).load(uri.toString()).into(imageView);
                //TODO weird spacing
            }
        });
    }
}
