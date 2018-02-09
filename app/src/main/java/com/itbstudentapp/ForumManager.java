package com.itbstudentapp;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 *  Jobs -> Retrive posts from the database that the user will want
 */


public class ForumManager {

    private ForumManager instance = this;
    private ArrayList<ForumPost> posts;

    private LinearLayout listView;
    private View forumDisplay;
    private String messagePath;

    public ForumManager(String messagePath)
    {
        this.messagePath = messagePath;
    }

    View forumDis;

    public void addPostToView(final LinearLayout v, final String path)
    {
        listView = v;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(posts == null)
                    posts = new ArrayList<ForumPost>();

              //  final View forumDis = LayoutInflater.from(v.getContext()).inflate(R.layout.forum_topic_post, null);
                final ForumPost forumPost = dataSnapshot.getValue(ForumPost.class);

                if(forumPost.getFileUpload() != null)
                {
                    forumDis = LayoutInflater.from(v.getContext()).inflate(R.layout.forum_topic_post_image, null);
                    final ImageView imageView =forumDis.findViewById(R.id.forum_user_post_image);
                    final StorageReference storage = FirebaseStorage.getInstance().getReference("forumImages/").child(forumPost.getFileUpload());
                    Glide.with(v.getContext()).using(new FirebaseImageLoader()).load(storage).into(imageView);
                } else{

                    forumDis = LayoutInflater.from(v.getContext()).inflate(R.layout.forum_topic_post, null);
                }

                Button replyButton = forumDis.findViewById(R.id.forum_see_posts);
                replyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new CommentsModal(forumPost, forumDis, path);
                    }
                });

                addMessageFunctionToView(forumPost.getPosterID(),(LinearLayout) forumDis.findViewById(R.id.forum_user_section));
                listView.addView(buildPostSection(forumPost, forumDis));
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                //TODO fix so child count goes up on forum screen.
            }
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    private void addMessageFunctionToView(final String posterID, final LinearLayout forumDis) {

        if(posterID.equalsIgnoreCase(UtilityFunctions.getUserNameFromFirebase()))
         //   return; // TODO uncomment so we dont message ourselves

       forumDis.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               PopupMenu popupMenu = new PopupMenu(forumDis.getContext(), forumDis);
               popupMenu.getMenuInflater().inflate(R.menu.user_contact_menu, popupMenu.getMenu());

               popupMenu.show();

               popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                   @Override
                   public boolean onMenuItemClick(MenuItem item) {

                       if(item.getTitle().toString().equalsIgnoreCase("contact"))
                       {
                           Intent messageUser = new Intent(listView.getContext(), MessageScreen.class);
                           messageUser.putExtra("message_id", posterID);
                           listView.getContext().startActivity(messageUser);
                       }

                       return true;
                   }
               });
           }
       });
    }

    private View buildPostSection(final ForumPost post, final View forumPost )
    {
        final TextView forumComment = forumPost.findViewById(R.id.forum_user_post);
        forumComment.setText(post.getPostComment());

        final TextView postNumber = forumPost.findViewById(R.id.post_number);

        if(post.getPostReplies().size() > 0) {
            postNumber.setText(post.getPostReplies().size() + " posts");
            postNumber.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    new CommentsModal(post, forumPost, messagePath);
                }
            });

        } else{
            postNumber.setVisibility(TextView.INVISIBLE);
        }

        getPosterInformation(post.getPosterID(), forumPost);


        return forumPost;
    }

    private void getPosterInformation(final String user_id, final View forumDisplay)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView userName = forumDisplay.findViewById(R.id.forum_user_name);
                userName.setText(dataSnapshot.getKey());

                TextView userId = forumDisplay.findViewById(R.id.forum_user_id);
                userId.setText(user_id);

                // add picture code here
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
/**
    private void addOneToPostCount() // nope
    {
        TextView text = forumDis.findViewById(R.id.post_number);

        if(text.getText().length() == 0 || text.getText() == null)
        {
            text.setVisibility(View.VISIBLE);
            text.setText("1 posts");
        } else{
            int postAmount = Integer.parseInt(String.valueOf(text.getText().charAt(0)));
            postAmount++;
            text.setText(postAmount + " posts");
        }
    } */
}
