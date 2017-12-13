package com.itbstudentapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginScreen extends AppCompatActivity implements View.OnClickListener{

    private EditText user, password;
    private Button login, forgotten_password, register;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        user = (EditText) findViewById(R.id.user_name_field);
        password = (EditText) findViewById(R.id.password_field);

        login = (Button) findViewById(R.id.login_button);
        register = (Button) findViewById(R.id.register_button);
        forgotten_password = (Button) findViewById(R.id.forgotten_password);

        login.setOnClickListener(this);
        register.setOnClickListener(this);
        forgotten_password.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null)
        {
            Log.e("User", "onCreate: " + auth.getCurrentUser().getEmail());
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void LoginUser()
    {
        final String user_name = user.getText().toString();
        final String user_password = password.getText().toString();

        if(user_name.length() == 0 || TextUtils.isEmpty(user_name))
        {
            Toast.makeText(getApplicationContext(), "user_namename must not be blank.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length() == 0 || TextUtils.isEmpty(user_password))
        {
            Toast.makeText(getApplicationContext(), "Password must not be blank.", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(user_name, user_password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    Intent intent = new Intent(LoginScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                 else {
                    Toast.makeText(getApplicationContext(), "Sign in failed.", Toast.LENGTH_SHORT);
                    user.setText("");
                    password.setText("");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.login_button)
        {
            LoginUser();
        } else if(v.getId() == R.id.register_button)
        {
            Intent intent = new Intent(this, RegisterUser.class);
            startActivity(intent);
            finish();
        } else{
            // forgotten password
        }
    }
}
