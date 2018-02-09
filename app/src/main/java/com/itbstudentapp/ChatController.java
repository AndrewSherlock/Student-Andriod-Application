package com.itbstudentapp;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.*;


import java.util.ArrayList;
import java.util.Calendar;

public class ChatController {

    private Activity controllingActivity;

    private ArrayList<String> receiversList;
    private ArrayList<ContactCard> usersContactCards;

    private String imageLink;

    /************************************************************************************/

    //TODO it writes multiple times the same message, deleting the first message.

    /***********************************************************************************/

    public ChatController(Activity controllingActivity) {
        this.controllingActivity = controllingActivity;
    }

    public void setUpNewMessage() {
        usersContactCards = new ArrayList<ContactCard>();
        receiversList = new ArrayList<String>();
        receiversList.add(UtilityFunctions.getUserNameFromFirebase());
        getListOfUsers();

        /*********** Dev ******************/
        //receiversList.add("b00091705");
        receiversList.add("sherlock-r32");


        /*********************************/
    }

    public void chooseUsersFromList(TextView recieverList) {
        Dialog dialog = new Dialog(controllingActivity);
        dialog.setContentView(R.layout.contact_list);
        dialog.show();

        //TODO redesign the pop up for groups and overall a better system
    }


    /********* Message sending *********************/

    boolean hasSent;

    /**
     * This check the message for length and internet connection. if we have both, message moves to next stage
     *
     * @param userMessage user message is the textField that the text belongs to
     */
    public void beginMessageSending(EditText userMessage) {
        String messageToSend = userMessage.getText().toString();
        hasSent = false;

        if (!UtilityFunctions.doesUserHaveConnection(controllingActivity)) {
            Toast.makeText(controllingActivity, "No internet connection. Please wait to reconnect.", Toast.LENGTH_SHORT).show();
            return;
        } else if (messageToSend.length() <= 0) {
            Toast.makeText(controllingActivity, "Please enter a message to send", Toast.LENGTH_SHORT).show();
            return;
        }

        Message message = new Message(receiversList.get(0), messageToSend, Calendar.getInstance().getTimeInMillis());
        userMessage.setText("");

        if (imageLink != null) {
            //TODO function to upload image
        }

        checkDatabaseLocation(receiversList, message);
    }


    private void addMessageToDatabase(ArrayList<String> users, Message message, long currentIndex)
    {
        for(int i = 0; i < users.size(); i++)
        {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("users/" + users.get(i) +"/messages/" + buildAddress(users.get(i), users)+ "/" + currentIndex);
            String read_status = ((UtilityFunctions.getUserNameFromFirebase().equalsIgnoreCase(users.get(i))? "1": "0" ));
            db.getParent().child("read_status").setValue(read_status);
            db.getParent().child("time_stamp").setValue(-message.getSendTime());

            db.setValue(message);
        }
    }

    int index = 0;

    private void checkDatabaseLocation(final ArrayList<String> messageRecievers, final Message message) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + receiversList.get(0) + "/messages/");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String[] users = dataSnapshot.getKey().split(",");
                boolean isContained = true;
                for (int i = 0; i < users.length; i++) {
                    if ((receiversList.size() - 1) != users.length) // we are also in the reciever list so we need to subtract an extra 1
                    {
                        isContained = false;
                        break;
                    }

                    //TODO i am not convinced by this method. test it!!!!! was buggy
                    if (!receiversList.contains(users[i])) {
                        isContained = false;
                        break;
                    }

                    index = (int) dataSnapshot.getChildrenCount();
                }

                if (isContained && index > 0) {
                    addMessageToDatabase(messageRecievers, message, (int) dataSnapshot.getChildrenCount());
                } else if (index == 0) {

                    addMessageToDatabase(messageRecievers, message, 1);
                }
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /********** End of message sending ************/

    /********* Loading messages *****************/

    int messageCounter = 0;

    public void loadMessages(String messageKey, final LinearLayout view, final ScrollView scrollView) {
        addUsersFromChatToList(messageKey);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/" + UtilityFunctions.getUserNameFromFirebase() + "/messages/" + messageKey);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.getKey().equalsIgnoreCase("read_status"))
                    return;

                Message currentMessage = dataSnapshot.getValue(Message.class);

                View v = LayoutInflater.from(view.getContext()).inflate(R.layout.message_dialog_box, null);
                RelativeLayout dialog_box = v.findViewById(R.id.chat_message_dialog_box);

                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.scrollTo(0, scrollView.getBottom() + 60);
                    }
                });

                if (currentMessage.getSender().equalsIgnoreCase(UtilityFunctions.getUserNameFromFirebase())) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dialog_box.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    // dialog_box.setBackgroundColor(Color.parseColor("#91bbff"));
                }

                TextView userName = v.findViewById(R.id.chat_message_user_name);
                userName.setText(currentMessage.getSender());

                TextView user_message = v.findViewById(R.id.chat_message_user_message);
                user_message.setText(currentMessage.getMessage());

                TextView time_display = v.findViewById(R.id.chat_message_date);
                time_display.setText(UtilityFunctions.milliToTime(currentMessage.getSendTime()));

                if (currentMessage.getImageLink() != null) {
                    ImageView imageView = v.findViewById(R.id.chat_message_image);
                    new ImageController().setImageInView(imageView, "chatImages/" + currentMessage.getImageLink());
                    imageView.setVisibility(View.VISIBLE);
                }

                view.addView(v);

            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /****************************** NOT GONNA CUT IT **********************************/
    public String buildAddress(String user, ArrayList<String> usersList) {
        String address = "";

        for (int i = 0; i < usersList.size(); i++) {
            if (user.equalsIgnoreCase(usersList.get(i)))
                continue; // skips adding the senders name to the address in db

            address += usersList.get(i);

            if (i < usersList.size() - 1 && usersList.size() > 2) {
                address += ",";
            }
        }

        return address;
    }


    /*********************************************************************************/

    private void addUsersFromChatToList(String messageList) {
        receiversList = new ArrayList<String>();
        receiversList.add(UtilityFunctions.getUserNameFromFirebase().toLowerCase());
        String users[] = messageList.split(",");

        for (int i = 0; i < users.length; i++)
            receiversList.add(users[i].toLowerCase());
    }

    public boolean hasRecievers() {
        return (receiversList.size() >= 2); // we must be in the reciever list
    }


    private void getListOfUsers() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ContactCard contactCard = new ContactCard(dataSnapshot.getKey(),
                        dataSnapshot.child("username").getValue().toString(),
                        dataSnapshot.child("username").getValue().toString());

                usersContactCards.add(contactCard);
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public class ContactCard {
        private String user_id;
        private String user_name;
        private String user_image;

        public ContactCard(String user_id, String user_name, String user_image) {
            this.user_id = user_id;
            this.user_name = user_name;
            this.user_image = user_image;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getUser_image() {
            return user_image;
        }

        public void setUser_image(String user_image) {
            this.user_image = user_image;
        }
    }

}

