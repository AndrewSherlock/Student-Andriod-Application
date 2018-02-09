package com.itbstudentapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddClass extends AppCompatActivity {

    private Button btnSave;
    private EditText class_event, day, time, room;

    DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);
        btnSave = findViewById(R.id.btnSaveTimetableEntry);
        class_event = findViewById(R.id.addClassOrEvent);
        day = findViewById(R.id.addTimetableDay);
        time = findViewById(R.id.addTimetableTime);
        room = findViewById(R.id.addTimetableRoom);
        databaseHelper = new DatabaseHelper(this);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String class_event = AddClass.this.class_event.getText().toString();
                String day = AddClass.this.day.getText().toString();
                String time = AddClass.this.time.getText().toString();
                String room = AddClass.this.room.getText().toString();
                if(!class_event.equals("")){
                    databaseHelper.addData(time, class_event, day, room);
                    toastMessage("Saved");
                    Intent intent = new Intent(AddClass.this, DayView.class);
                    intent.putExtra("day",day);
                    startActivity(intent);
                }else{
                    toastMessage("You must enter a class or event");
                }
            }
        });
    }
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
