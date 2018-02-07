package com.itbstudentapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    public void registerUser(String username, String emailAddress, String password)
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
        //Dialog courseChoice = new Dialog(context);
        //courseChoice.setContentView(R.layout.forum_comments_modal);

        String course = "bn104";


        writeToDatabase(getUserAccountType(emailAddress), course);
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

        User user = new User(username, password, courseId, accountType, emailAddress, true);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(prepareFirebaseLink(emailAddress.split("@")[0])).setValue(user);

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
