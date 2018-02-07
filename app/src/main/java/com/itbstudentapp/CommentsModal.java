package com.itbstudentapp;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class CommentsModal{

    private View forumDis;
    private ForumPost forumPost;
    private String path;
    private Dialog dialog;
   private boolean completeFlags[];
   private boolean shouldListen = false;
   private String topicPath;

    public CommentsModal(ForumPost forumPost, View forumDis, String path)
    {
        this.forumDis = forumDis;
        this.forumPost = forumPost;
        this.path = path;

        dialog = new Dialog(forumDis.getContext());
        dialog.setContentView(R.layout.forum_comments_modal);
        completeFlags = new boolean[forumPost.getPostReplies().size()];

        for(int i = 0; i < completeFlags.length; i++)
            completeFlags[i] = false;

        topicPath = path + "/" + forumPost.getPostTitle();


        addCommentsToModal(dialog);
        setUpReplyView(dialog);

        dialog.show();
    }

    private void setUpReplyView(final Dialog dialog)
    {
        final EditText replyMessage = dialog.findViewById(R.id.forum_modal_text_box);

        Button replyButton = dialog.findViewById(R.id.forum_modal_reply_button);
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String reply = replyMessage.getText().toString();

                if(reply.length() <= 0)
                {
                    Toast.makeText(dialog.getContext(), "Message can not be blank", Toast.LENGTH_SHORT).show();
                    return;
                }

                replyMessage.setText("");
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(topicPath);
                Reply user_reply = new Reply(UtilityFunctions.getUserNameFromFirebase(), reply, Calendar.getInstance().getTimeInMillis());

                reference.child("postReplies").child(String.valueOf(forumPost.getPostReplies().size())).setValue(user_reply);

                addMessageToView(dialog,  user_reply, UtilityFunctions.getUserNameFromFirebase());
            }
        });
    }

    private void addCommentsToModal(final Dialog dialog)
    {
        final ArrayList<Reply> replies = forumPost.getPostReplies();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        setupListenerForNewMessages(topicPath);

        if(replies.size() == 0 || replies == null) // we have no messages yet so we inform the user and leave method
        {
            View no_reply = LayoutInflater.from(dialog.getContext()).inflate(R.layout.forum_topic_no_reply, null);
            LinearLayout linearLayout = dialog.findViewById(R.id.forum_modal_comment_list);
            linearLayout.addView(no_reply);
            return;
        }

        for(final Reply r : replies)
        {
            ref.child(r.getPosterID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    addMessageToView(dialog,  r, dataSnapshot.child("username").getValue(String.class));

                    completeFlags[replies.indexOf(r)] = true;
                    checkForListen();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void addMessageToView(final Dialog dialog, Reply reply, final String username)
    {
        //TODO check if the view has the no replies message and remove if it does

        final LinearLayout linearLayout = dialog.findViewById(R.id.forum_modal_comment_list);
        View view = LayoutInflater.from(dialog.getContext()).inflate(R.layout.modal_reply, null);

        TextView userName = view.findViewById(R.id.forum_modal_user_comment_name);

        //TODO make sure theres no listener for ouselves
        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(dialog.getContext(), linearLayout);
                popupMenu.getMenuInflater().inflate(R.menu.user_contact_menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if(item.getTitle().toString().equalsIgnoreCase("contact"))
                        {
                            Intent messageUser = new Intent(linearLayout.getContext(), MessageScreen.class);
                            messageUser.putExtra("message_id", username);
                            dialog.getContext().startActivity(messageUser);
                            dialog.dismiss();
                        }

                        return true;
                    }
                });
            }
        });


        userName.setText(username);

        TextView userComment = view.findViewById(R.id.forum_modal_comment_reply);
        userComment.setText(reply.getPosterComment());

        linearLayout.addView(view);
    }

    private void setupListenerForNewMessages(String path)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path + "/postReplies");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if(!shouldListen) {
                    checkForListen();
                    return;
                }
                Reply r = dataSnapshot.getValue(Reply.class);

                if(!r.getPosterID().equalsIgnoreCase( UtilityFunctions.getUserNameFromFirebase()))
                {
                    addMessageToView(dialog, r, r.getPosterID());
                    addOneToPostCount();
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

    private void checkForListen() {

        // this is used as when we are initializing our screens, we dont want to populated the text more then once.
        // this is a method of blocking it till we have fully initialized. after the should listen is set. our new message listener works

        for(int i = 0; i < completeFlags.length; i++)
        {
            if(completeFlags[i] == false){
                return;
            }
        }

        shouldListen = true;
    }

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
    }
}
