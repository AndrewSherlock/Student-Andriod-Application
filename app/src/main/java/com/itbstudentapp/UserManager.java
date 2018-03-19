package com.itbstudentapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserManager
{

    private Context context;

    private String username;
    private String password;
    private String emailAddress;

    public UserManager(Context context)
    {
        this.context = context;
    }

    public boolean isTheUserEmailValid(String email)
    {
        String[] emailAddress = context.getResources().getStringArray(R.array.permitted_emails);

        for(int i = 0; i < emailAddress.length; i++)
        {
            if(emailAddress[i].equalsIgnoreCase(email))
                return true;
        }

        return false;
    }

    public boolean areFieldsBlank(String ... texts)
    {
        for(int i = 0; i < texts.length; i++)
        {
            if(texts[i].length() <= 0)
                return true;
        }

        return false;
    }

    private boolean validPasswordFormat(String password)
    {
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasNumber = false;
        boolean numberOfCharacters = (password.length() >= 8);

        for(int i = 0; i < password.length(); i++)
        {
            if(!numberOfCharacters)
                return numberOfCharacters;

            if(Character.isUpperCase(password.charAt(i)))
                hasUppercase = true;

            if(Character.isLowerCase(password.charAt(i)))
                hasLowercase = true;

            if(Character.isDigit(password.charAt(i)))
                hasNumber = true;

           if(Character.isSpaceChar(password.charAt(i)))
               return false;
        }

        return hasUppercase && hasLowercase && hasNumber;
    }

    public void registerUser(final String username, String emailAddress, String password)
    {
        if(areFieldsBlank(username, emailAddress, password))
        {
            Toast.makeText(context, "Ensure that the fields are not blank", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isTheUserEmailValid(emailAddress.split("@")[1]))
        {
            for(String s: emailAddress.split("@"))
                Log.e("String", s);

            Toast.makeText(context, "Your email is not an ITB email address.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!validPasswordFormat(password))
        {
            Toast.makeText(context, "Invalid password format", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(username.split("@")[0]).exists())
                {
                    Toast.makeText(context, "User is already signed up.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        this.username = username;
        this.emailAddress = emailAddress.toLowerCase();
        this.password = password;

        if(this.emailAddress.contains("student"))
            askUserForCourse();
        else
           writeToDatabase(getUserAccountType(emailAddress), null);
    }

    private void askUserForCourse()
    {
        final Dialog courseChoice = new Dialog(context);
        courseChoice.setContentView(R.layout.modal_course_choice);
        LinearLayout modalLayout = courseChoice.findViewById(R.id.course_list);

        final String[] courses = context.getResources().getStringArray(R.array.courses);
       
        for(int i = 0; i < courses.length; i++)
        {
            View courseItem = LayoutInflater.from(context).inflate(R.layout.course_item_list,null);
            final TextView textView = courseItem.findViewById(R.id.course_title);
            textView.setText(courses[i]);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    writeToDatabase(getUserAccountType(emailAddress), textView.getText().toString());
                    courseChoice.dismiss();
                }
            });

            modalLayout.addView(courseItem);
        }

        courseChoice.show();
    }

    private String getUserAccountType(String emailAddress)
    {
        String emailLink = emailAddress.split("@")[1];

        if(emailLink.contains("student"))
            return "student";

        if(emailLink.contains("admin"))
            return "admin";

        if(emailLink.contains("nln"))
            return "nln";

        return "itb-staff";
    }

    private void writeToDatabase(String accountType, String courseId)
    {
        if(!UtilityFunctions.doesUserHaveConnection(context))
        {
            Toast.makeText(context, "Please wait for network connection.", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(username, courseId, accountType, emailAddress, true);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(prepareFirebaseLink(emailAddress.split("@")[0])).setValue(user);
        databaseReference.child("unapproved_users").child(prepareFirebaseLink(emailAddress.split("@")[0])).setValue(prepareFirebaseLink(emailAddress.split("@")[0]));

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(emailAddress, password);

        Toast.makeText(context, "Account created successfully.", Toast.LENGTH_SHORT).show();
        context.startActivity(new Intent(context, LoginScreen.class));
    }

    public String prepareFirebaseLink(String id)
    {
       return id.replace('.', '_');
    }
}
