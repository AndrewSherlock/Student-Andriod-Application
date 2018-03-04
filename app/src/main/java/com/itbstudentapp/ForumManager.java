package com.itbstudentapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itbstudentapp.NotificationSystem.FirebaseNotificationManager;
import com.itbstudentapp.NotificationSystem.Notification;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class ForumManager {

    private Context context;
    private boolean shouldListenToReplies;

    public ForumManager() {
    }

    public ForumManager(Activity activity) {
        this.context = activity;
    }

    public ForumManager(Context context) {
        this.context = context;
    }

    int counter;

    public void getMenuFromDB(final GridLayout gridLayout, final String key, final ProgressDialog progress) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("forum/" + key);
        final ArrayList<String> topic_name = new ArrayList<String>();


        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (final DataSnapshot data : dataSnapshot.getChildren()) {
                    boolean isPermitted = false;

                    if (key.equalsIgnoreCase("forum_module")) {
                        counter++;
                        String permittedCourse = data.child("permitted").getValue(String.class);
                        topic_name.add(UtilityFunctions.formatTitles(data.getKey()));
                        if (permittedCourse != null) {
                            String[] courses = permittedCourse.split("_");
                            for (int i = 0; i < courses.length; i++) {
                                courses[i] = courses[i].trim();

                                if (courses[i].equalsIgnoreCase(UtilityFunctions.studentCourse)) {
                                    isPermitted = true;
                                    break;
                                }
                            }
                        }
                    } else if (key.equalsIgnoreCase("forum_groups")) {
                        counter++;
                        topic_name.add(UtilityFunctions.formatTitles(data.getKey()));
                        String permitted = data.child("permitted").getValue(String.class);
                        if (permitted.equalsIgnoreCase("all") || (permitted.equalsIgnoreCase(UtilityFunctions.student_group))) {
                            isPermitted = true;
                        }
                    }

                    if (isPermitted) {
                        View view = LayoutInflater.from(gridLayout.getContext()).inflate(R.layout.forum_section_panel, null);
                        TextView text = view.findViewById(R.id.section_text_box);
                        text.setText(UtilityFunctions.formatTitles(data.getKey()));

                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(gridLayout.getContext(), ForumList.class);
                                intent.putExtra("path", key + "/" + data.getKey());
                                gridLayout.getContext().startActivity(intent);
                            }
                        });

                        gridLayout.addView(view);
                    }
                }

                progress.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addNewUserPost(String forumSection) {

        NewPostModal newPostModal = NewPostModal.newInstance(forumSection);
        newPostModal.show(fragmentManager, "new_post");
    }

    public boolean addNewPostToDatabase(final String forumLink, ForumPost forumPost, final Uri image, final NewPostModal newPostModal) {
        if (!UtilityFunctions.doesUserHaveConnection(context)) {
            Toast.makeText(context, "No network connection", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (image != null) {
            String image_path = UUID.randomUUID().toString();
            forumPost.setFileUpload(image_path);

            uploadImage(image, image_path);
        }

        final ForumPost post = forumPost;
        Thread delayThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (image != null && !hasUploaded) ;

                Looper.prepare();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("forum/" + forumLink + "/topics/");
                reference.child(post.getPostTitle()).setValue(post);
                reference.child(post.getPostTitle()).child("subscribed_users").child(UtilityFunctions.getUserNameFromFirebase()).setValue(UtilityFunctions.getUserNameFromFirebase() + ",");
                Toast.makeText(context, "Topic posted successfully", Toast.LENGTH_SHORT).show();
                sendNotificationsToUsers(reference.child(post.getPostTitle()).child("subscribed_users").getRef());
                newPostModal.postMessage();
            }
        });

        delayThread.start();

        return true;
    }

    private void sendNotificationsToUsers(final DatabaseReference reference)
    {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String userList = dataSnapshot.getValue(String.class);

                String[] usersArray = userList.split(",");
                Log.e("AG", "onDataChange: " + dataSnapshot.getRef( ) + "/" +dataSnapshot.getChildrenCount());

                for(DataSnapshot name : dataSnapshot.getChildren())
                {
                    //String notificationType, String title, String body, String messageSender
                    Notification notification = new Notification("forum", "New reply to a topic you posted in.", "", name.getKey().toLowerCase());
                    FirebaseNotificationManager.sendNotificationToUser(notification);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    boolean hasUploaded = false;

    private void uploadImage(Uri image, final String fileId) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();


        StorageReference ref = FirebaseStorage.getInstance().getReference().child("forumImages/" + fileId);
        ref.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Toast.makeText(context, "File uploaded", Toast.LENGTH_SHORT).show();
                hasUploaded = true;
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

    public void listenForNewTopics(final String forum_topic, final LinearLayout postSection) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("forum/" + forum_topic + "/topics/");
        reference.addChildEventListener(new ChildEventListener() {

            @Override

            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {

                final ForumPost forumPost = dataSnapshot.getValue(ForumPost.class);

                for (DataSnapshot d : dataSnapshot.child("replies").getChildren()) {
                    forumPost.addReplyToList(d.getValue(Reply.class));
                }

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(20, 20, 20, 20);

                View view = LayoutInflater.from(context).inflate(R.layout.forum_topic_post, null);
                view.setTag(dataSnapshot.getKey());
                TextView nameText = view.findViewById(R.id.forum_poster_name);
                final ImageView user_image = view.findViewById(R.id.forum_post_user_image);
                loadUserInformationIntoSection(nameText, user_image, forumPost.getPosterID());

                TextView dateText = view.findViewById(R.id.forum_post_date);
                dateText.setText(UtilityFunctions.milliToTime(forumPost.getPostTime()));

                TextView postText = view.findViewById(R.id.forum_post);
                postText.setText(forumPost.getPostComment());

                if (forumPost.getFileUpload() != null) {
                    final ImageView imageView = view.findViewById(R.id.forum_post_image);
                    StorageReference reference = FirebaseStorage.getInstance().getReference("forumImages/" + forumPost.getFileUpload());

                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {
                            final String imageLink = uri.toString();
                            Glide.with(context).load(imageLink).into(imageView);
                        }
                    });

                }

                TextView postNumberText = view.findViewById(R.id.forum_number_of_posts);
                if (forumPost.getPostReplies().size() > 0) {
                    postNumberText.setText(forumPost.getPostReplies().size() + " Posts");
                } else {
                    postNumberText.setVisibility(View.INVISIBLE);
                }

                final TextView newPost = view.findViewById(R.id.forum_reply_button);
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReplyModal replyModal = ReplyModal.newInstance();
                        replyModal.init(forumPost, forum_topic + "/topics/" + dataSnapshot.getKey());
                        replyModal.show(fragmentManager, "reply");
                    }
                };

                postNumberText.setOnClickListener(listener);
                newPost.setOnClickListener(listener);

                postSection.addView(view, 0, layoutParams);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void loadUserInformationIntoSection(final TextView nameText, final ImageView user_image, final String posterID) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + posterID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User forum_poster = dataSnapshot.getValue(User.class);

                if (forum_poster.getImageLink() != null) {
                    nameText.setText(forum_poster.getUsername());

                    StorageReference reference = FirebaseStorage.getInstance().getReference("forumImages/" + forum_poster.getImageLink());
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageLink = uri.toString();
                            Glide.with(context).load(imageLink).into(user_image);
                        }
                    });
                }

                addMessageFunctionToView(posterID, user_image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addReplysToModal(final LinearLayout messageSection, ForumPost forumPost) {
        final ArrayList<Reply> posts = forumPost.getPostReplies();
        shouldListenToReplies = false;
        for (int i = 0; i < posts.size(); i++) {
            final int counter = i;
            final View view = LayoutInflater.from(context).inflate(R.layout.forum_post_section_modal, null);
            TextView reply = view.findViewById(R.id.forum_reply_comment);
            reply.setText(posts.get(i).getPosterComment());

            TextView timeText = view.findViewById(R.id.forum_reply_date);
            timeText.setText(UtilityFunctions.milliToTime(posts.get(i).getPostTime()));

            final ImageView postImage = view.findViewById(R.id.forum_reply_image);
            StorageReference storage = FirebaseStorage.getInstance().getReference("forumImages/" + posts.get(i).getImageLink());
            storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String imageLink = uri.toString();
                    Glide.with(context).load(imageLink).into(postImage);
                }
            });

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/" + posts.get(i).getPosterID());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    TextView username = view.findViewById(R.id.forum_reply_name);
                    username.setText(user.getUsername());

                    final ImageView userImage = view.findViewById(R.id.forum_user_post_image);

                    if (user.getImageLink() != null) {
                        StorageReference reference = FirebaseStorage.getInstance().getReference("forumImages/" + user.getImageLink());
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageLink = uri.toString();
                                Glide.with(context).load(imageLink).into(userImage);
                            }
                        });
                    }

                    addMessageFunctionToView(dataSnapshot.getKey(), userImage);
                    Log.d("teste", "onDataChange: " + dataSnapshot.getKey() + " "  + userImage);
                    messageSection.addView(view);

                    if (counter == posts.size() - 1) {
                        shouldListenToReplies = true;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public boolean addReplyToDatabase(final String postLink, final int size, final String posterComment, final Uri image) {
        Reply reply = new Reply(UtilityFunctions.getUserNameFromFirebase(), posterComment, Calendar.getInstance().getTimeInMillis());


        if (!UtilityFunctions.doesUserHaveConnection(context)) {
            Toast.makeText(context, "No network connection, Please try again", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (image != null) {
            String image_path = UUID.randomUUID().toString();
            reply.setImageLink(image_path);

            uploadImage(image, image_path);
        }

        final Reply r = reply;

        Thread delayThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (image != null && !hasUploaded) ;

                Looper.prepare();
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("forum/" + postLink + "/replies/");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long count = dataSnapshot.getChildrenCount();

                        reference.child(String.valueOf(count)).setValue(r);
                        reference.getParent().child("subscribed_users").child(UtilityFunctions.getUserNameFromFirebase()).setValue(UtilityFunctions.getUserNameFromFirebase());
                        Toast.makeText(context, "Reply posted successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        delayThread.start();
        return true;
    }

    private void addMessageFunctionToView(final String posterID, final ImageView userLayout) {

        if (posterID.equalsIgnoreCase(UtilityFunctions.getUserNameFromFirebase()))
            return;

        userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(userLayout.getContext(), userLayout);
                popupMenu.getMenuInflater().inflate(R.menu.user_contact_menu, popupMenu.getMenu());

                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getTitle().toString().equalsIgnoreCase("contact")) {
                            Intent messageUser = new Intent(context, MessageScreen.class);
                            messageUser.putExtra("message_id", posterID);
                            context.startActivity(messageUser);
                        }

                        return true;
                    }
                });
            }
        });
    }

    FragmentManager fragmentManager;

    public void giveFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void listenForReplys(final LinearLayout messageSection, String postLink)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(postLink + "/replies/");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (shouldListenToReplies) {
                    Log.e("Child", "onChildAdded: " + "new child" );
                    final View view = LayoutInflater.from(context).inflate(R.layout.forum_post_section_modal, null);
                    final Reply r = dataSnapshot.getValue(Reply.class);

                    TextView reply = view.findViewById(R.id.forum_reply_comment);
                    reply.setText(r.getPosterComment());

                    TextView timeText = view.findViewById(R.id.forum_reply_date);
                    timeText.setText(UtilityFunctions.milliToTime(r.getPostTime()));

                    final ImageView postImage = view.findViewById(R.id.forum_reply_image);
                    StorageReference storage = FirebaseStorage.getInstance().getReference("forumImages/" + r.getImageLink());
                    storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageLink = uri.toString();
                            Glide.with(context).load(imageLink).into(postImage);
                        }
                    });

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/" + r.getPosterID());
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            TextView username = view.findViewById(R.id.forum_reply_name);
                            username.setText(user.getUsername());

                            final ImageView userImage = view.findViewById(R.id.forum_user_post_image);
                            addMessageFunctionToView(r.getPosterID(), userImage);

                            if (user.getImageLink() != null) {
                                StorageReference reference = FirebaseStorage.getInstance().getReference("forumImages/" + user.getImageLink());
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageLink = uri.toString();
                                        Glide.with(context).load(imageLink).into(userImage);
                                    }
                                });
                            }
                        }


                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    messageSection.addView(view);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
