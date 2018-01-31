package com.itbstudentapp;

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

public class Quiz extends AppCompatActivity implements View.OnClickListener{

    // I used this tutorial to make this
    //https://www.youtube.com/watch?v=-4bZ_rfvBTk&t

    private TextView mScoreView;
    private TextView mQuestion;

    private Button buttons[];

    private int mScore = 0;
    private int mQuestionNumber = 0;
    private String mAnswer;

    private DatabaseReference ref;
  //  private FirebaseDatabase mQuestionRef, mchoice1Ref, mchoice2Ref,mchoice3Ref, mchoice4Ref,mAnswerRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);



        mScoreView = (TextView)findViewById(R.id.score);
        mQuestion = (TextView)findViewById(R.id.question);

        buttons = new Button[4];
        buttons[0] = (Button)findViewById(R.id.choice1);
        buttons[1] = (Button)findViewById(R.id.choice2);
        buttons[2] = (Button)findViewById(R.id.choice3);
        buttons[3] = (Button)findViewById(R.id.choice4);

        ref = FirebaseDatabase.getInstance().getReference("/quiz");

        buttons[0].setOnClickListener(this);
        buttons[1].setOnClickListener(this);
        buttons[2].setOnClickListener(this);
        buttons[3].setOnClickListener(this);

        /* this can be made a lot easier then this
        //button1
        mButtonChoice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mButtonChoice1.getText().equals(mAnswer)){
                    mScore = mScore+1;
                    updateScore(mScore);
                    updateQuestion();
                }else{
                    updateQuestion();
                }
            }
        });
        //button2
        mButtonChoice2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mButtonChoice2.getText().equals(mAnswer)){
                    mScore = mScore+1;
                    updateScore(mScore);
                    updateQuestion();
                }else{
                    updateQuestion();
                }
            }
        });
        //button3
        mButtonChoice3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mButtonChoice3.getText().equals(mAnswer)){
                    mScore = mScore+1;
                    updateScore(mScore);
                    updateQuestion();
                }else{
                    updateQuestion();
                }
            }
        });
        //button4
        mButtonChoice4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mButtonChoice4.getText().equals(mAnswer)){
                    mScore = mScore+1;
                    updateScore(mScore);
                    updateQuestion();
                }else{
                    updateQuestion();
                }
            }
        });
        */

        updateQuestion();

    }



    private void updateScore(int score){
        mScoreView.setText(" "+ mScore);
    }

    private void updateQuestion(){
        Log.e("Called", "called");
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
        /*
        mQuestionRef = new Firebase("https://itb-student-app-6727d.firebaseio.com/quiz/"+ mQuestionNumber +"/question");
        mQuestionRef.addValueEventListener(new ValueEventListener(){

            public void onDataChange(DataSnapshot dataSnapshot) {
                String question = dataSnapshot.getValue(String.class);
                mQuestion.setText(question);
            }

            public void onCancelled (FirebaseError firebaseError){

            }
        });

        mchoice1Ref = new Firebase("https://itb-student-app-6727d.firebaseio.com/quiz/"+ mQuestionNumber +"/choice1");
        mchoice1Ref.addValueEventListener(new ValueEventListener(){

            public void onDataChange(DataSnapshot dataSnapshot){
                String choice = dataSnapshot.getValue(String.class);
                mButtonChoice1.setText(choice);
            }
            public void onCancelled (FirebaseError firebaseError){

            }

        });
        mchoice2Ref = new Firebase("https://itb-student-app-6727d.firebaseio.com/quiz/"+ mQuestionNumber +"/choice2");
        mchoice2Ref.addValueEventListener(new ValueEventListener(){

            public void onDataChange(DataSnapshot dataSnapshot){
                String choice = dataSnapshot.getValue(String.class);
                mButtonChoice2.setText(choice);
            }
            public void onCancelled (FirebaseError firebaseError){

            }

        });
        mchoice3Ref = new Firebase("https://itb-student-app-6727d.firebaseio.com/quiz/ "+ mQuestionNumber +"/choice3");
        mchoice3Ref.addValueEventListener(new ValueEventListener(){

            public void onDataChange(DataSnapshot dataSnapshot){
                String choice = dataSnapshot.getValue(String.class);
                mButtonChoice3.setText(choice);
            }
            public void onCancelled (FirebaseError firebaseError){

            }

        });
        mchoice4Ref = new Firebase("https://itb-student-app-6727d.firebaseio.com/quiz/"+ mQuestionNumber +"/choice4");
        mchoice4Ref.addValueEventListener(new ValueEventListener(){

            public void onDataChange(DataSnapshot dataSnapshot){
                String choice = dataSnapshot.getValue(String.class);
                mButtonChoice4.setText(choice);
            }
            public void onCancelled (FirebaseError firebaseError){

            }

        });

        mAnswerRef = new Firebase ("https://itb-student-app-6727d.firebaseio.com/quiz/"+ mQuestionNumber +"/answer");
        mAnswerRef.addValueEventListener(new ValueEventListener(){

            public void onDataChange(DataSnapshot dataSnapshot){
                mAnswer = dataSnapshot.getValue(String.class);

            }
            public void onCancelled (FirebaseError firebaseError){

            }

        }); */
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
