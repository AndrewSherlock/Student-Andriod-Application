package com.itbstudentapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener{

    private Button submit, reset;
    private RadioButton staff, student;
    private EditText user_name, email, password, reenter;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        submit = (Button) findViewById(R.id.reg_submit);
        reset = (Button) findViewById(R.id.reg_reset);

        staff = (RadioButton) findViewById(R.id.staff_acc);
        student = (RadioButton) findViewById(R.id.student_acc);

        user_name = (EditText) findViewById(R.id.reg_user_name);
        email = (EditText) findViewById(R.id.reg_user_email);
        password = (EditText) findViewById(R.id.reg_user_password);
        reenter = (EditText) findViewById(R.id.reg_user_repeat);

        spinner = (Spinner) findViewById(R.id.course_choice);

        submit.setOnClickListener(this);
        reset.setOnClickListener(this);
    }

    private boolean ValidateUser(FirebaseAuth auth)
    {
       /* DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        Query search = db.child("users").equalTo();

        search.addListenerForSingleValueEvent(); */

       // Needs validation built




        return true;
    }

    private void AddUserToDatabase()
    {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        String username = user_name.getText().toString();
        String user_email = email.getText().toString();
        String user_password = password.getText().toString();
        boolean staffUser = staff.isSelected();
        String courseId = spinner.getSelectedItem().toString();

        String user_id = user_email.split("@")[0];

        User user = new User(username, user_password, courseId, staffUser, user_email);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(user_id).setValue(user);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(user_email, user_password);

        Toast.makeText(getApplicationContext(), "Account created.", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginScreen.class));

    }


    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.reg_submit)
        {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            boolean valid = ValidateUser(auth);

            if(valid)
            {
                AddUserToDatabase();
            } else
            {
                Toast.makeText(getApplicationContext(), "Information not valid. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } else if(v.getId() == R.id.reg_reset)
        {

        }
    }
}
