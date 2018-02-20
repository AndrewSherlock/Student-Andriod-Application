package com.itbstudentapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.Toast;

public class Quiz extends AppCompatActivity implements View.OnClickListener{

    /**
     * upload
     */

    private TextView mScoreView;
    private TextView mQuestion;

    private Button buttons[];

    private int mScore = 0;
    private int mQuestionNumber = 0;
    private String mAnswer;

    private DatabaseReference ref;

    private int numOfQuestions = 10;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);


        ref = FirebaseDatabase.getInstance().getReference("/quiz");
        getNumberOfQuestions();



        mScoreView = (TextView)findViewById(R.id.score);
        mQuestion = (TextView)findViewById(R.id.question);

        buttons = new Button[4];
        buttons[0] = (Button)findViewById(R.id.choice1);
        buttons[1] = (Button)findViewById(R.id.choice2);
        buttons[2] = (Button)findViewById(R.id.choice3);
        buttons[3] = (Button)findViewById(R.id.choice4);

        buttons[0].setOnClickListener(this);
        buttons[1].setOnClickListener(this);
        buttons[2].setOnClickListener(this);
        buttons[3].setOnClickListener(this);


        updateQuestion();

    }

    private void getNumberOfQuestions() {

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numOfQuestions = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void updateScore(int score){
        mScoreView.setText(" "+ mScore);
    }

    private void updateQuestion(){

        if(mQuestionNumber >= numOfQuestions)
        {
            // this is where you need to sort out the end of questions thing,
            Intent intent = new Intent(this, MainActivity.class);
            finish();
            if(numOfQuestions ==mScore){
                Toast.makeText(getApplicationContext(), "Wow you got top marks " + mScore + " out of " + mQuestionNumber, Toast.LENGTH_SHORT).show();
            }
            else if(mScore >5){
                Toast.makeText(getApplicationContext(), "Well done you got a score of " + mScore + " out of " + mQuestionNumber, Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(), "Looks like need get pencil out got score of " + mScore + " out of " + mQuestionNumber, Toast.LENGTH_SHORT).show();
            }
            return;

        }

        ref.child(String.valueOf(mQuestionNumber)).child("question").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mQuestion.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        for(int i = 0; i < 4; i++)
        {
            Log.e("Choice", String.valueOf(i));
            ref.child(String.valueOf(mQuestionNumber)).child("choice" + (i + 1)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String choice = dataSnapshot.getValue().toString();
                    String key = dataSnapshot.getKey();
                    int index = Integer.parseInt(String.valueOf(key.charAt(key.length() -1))) - 1;
                    Log.e("index", String.valueOf(Integer.parseInt(String.valueOf(key.charAt(key.length() -1))) - 1));
                    buttons[index].setText(choice);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

        }

        ref.child(String.valueOf(mQuestionNumber)).child("answer").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mAnswer = dataSnapshot.getValue().toString();
                Log.e("Answer", mAnswer);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mQuestionNumber++;

    }

    @Override
    public void onClick(View v) {

        Button answer = (Button) v.findViewById(v.getId());
        String user_choice = answer.getText().toString();

        if(user_choice.equalsIgnoreCase(mAnswer))
        {
            mScore = mScore+1;
            updateScore(mScore);

        }

        updateQuestion();


    }
}
