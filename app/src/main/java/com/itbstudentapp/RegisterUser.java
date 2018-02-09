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

   private Button register_user, clear_fields;
   private EditText user_name, user_password, user_repeat, user_email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        user_name = findViewById(R.id.register_user_name);
        user_email = findViewById(R.id.register_user_email);
        user_password = findViewById(R.id.register_user_password);
        user_repeat = findViewById(R.id.register_user_repeat);

        register_user = findViewById(R.id.register_submit_button);
        clear_fields = findViewById(R.id.register_clear_button);

        register_user.setOnClickListener(this);
        clear_fields.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == register_user.getId())
        {
            UserManager userManager = new UserManager(this);
            if(user_password.getText().toString().equals(user_repeat.getText().toString()))
                userManager.registerUser(user_name.getText().toString(), user_email.getText().toString(), user_password.getText().toString());
            else
                Toast.makeText(getApplicationContext(), "Your passwords don't match", Toast.LENGTH_SHORT);
        }

        if(v.getId() == clear_fields.getId())
            clearTextFields();
    }

    private void clearTextFields()
    {
        user_name.setText("");
        user_password.setText("");
        user_repeat.setText("");
        user_email.setText("");
    }
}
