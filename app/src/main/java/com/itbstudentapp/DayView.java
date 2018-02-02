package com.itbstudentapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.itbstudentapp.utils.LetterImageView;

public class DayView extends AppCompatActivity implements View.OnClickListener  {

    private ListView listView;
    private Button addNewClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_view);
        setupUIViews();
        setupListView();
        addNewClass = (Button) findViewById(R.id.addNewClass);


        addNewClass.setOnClickListener(this);
     }


    // this interputs the button which clicked
    public void onClick(View view) {

        if(view.getId() == R.id.addNewClass)
        {
            Intent intent = new Intent(this, AddClass.class);
            startActivity(intent);
        }
    }

    private void setupUIViews(){
        listView = findViewById(R.id.listCourseByDay);
    }

    private void setupListView() {
        String[] mondayClasses = getResources().getStringArray(R.array.Classes);
        DayAdapter adapter = new DayAdapter(this, R.layout.content_day_view, mondayClasses);
        listView.setAdapter(adapter);

    }

    public class DayAdapter extends ArrayAdapter{

        private int resource;
        private LayoutInflater layoutInflater;
        private String[] classes = new String[]{};

        public DayAdapter( Context context, int resource, String[] objects) {
            super(context, resource, objects);
            this.resource = resource;
            this.classes = objects;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null){
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(resource, null);
                viewHolder.letterImageView = convertView.findViewById(R.id.letterCircle);
                viewHolder.classTextView = convertView.findViewById(R.id.classTitle);
                viewHolder.classTimeText = convertView.findViewById(R.id.classTime);
                viewHolder.classRoomText = convertView.findViewById(R.id.classRoom);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }
            viewHolder.letterImageView.setOval(true);
            viewHolder.letterImageView.setLetter(classes[position].charAt(0));
            viewHolder.classTextView.setText(classes[position]);
            viewHolder.classTimeText.setText("9.00 - 11.00");
            viewHolder.classRoomText.setText("D025");

            return convertView;
        }

        class ViewHolder{
            private LetterImageView letterImageView;
            private TextView classTextView;
            private TextView classTimeText;
            private TextView classRoomText;
        }
    }
}
