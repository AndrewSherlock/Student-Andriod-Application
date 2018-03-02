package com.itbstudentapp;

import android.content.Context;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Random;

public class Timetable extends AppCompatActivity implements View.OnClickListener{

    private ListView listView;
    private TextView homeTextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeTextBtn = findViewById(R.id.timetableHomeBtn);
        homeTextBtn.setOnClickListener(this);
        setContentView(R.layout.activity_timetable);

        setupUIViews();
        setupListView();
    }

    private void setupUIViews(){
       listView=findViewById(R.id.timetable);
    }

    private void setupListView(){
        String[] days = getResources().getStringArray(R.array.WeekDays);

        SimpleAdapter simpleAdapter = new SimpleAdapter(Timetable.this, days);
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0:{
                        Intent intent = new Intent(Timetable.this, DayView.class);
                        intent.putExtra("day","Monday");
                        startActivity(intent);
                        break;
                    }
                    case 1:{
                        Intent intent = new Intent(Timetable.this, DayView.class);
                        intent.putExtra("day","Tuesday");
                        startActivity(intent);
                        break;
                    }
                    case 2:{
                        Intent intent = new Intent(Timetable.this, DayView.class);
                        intent.putExtra("day","Wednesday");
                        startActivity(intent);
                        break;
                    }
                    case 3:{
                        Intent intent = new Intent(Timetable.this, DayView.class);
                        intent.putExtra("day","Thursday");
                        startActivity(intent);
                        break;
                    }
                    case 4:{
                        Intent intent = new Intent(Timetable.this, DayView.class);
                        intent.putExtra("day","Friday");
                        startActivity(intent);
                        break;
                    }
                    case 5:{
                        Intent intent = new Intent(Timetable.this, DayView.class);
                        intent.putExtra("day","Saturday");
                        startActivity(intent);
                        break;
                    }
                    case 6:{
                        Intent intent = new Intent(Timetable.this, DayView.class);
                        intent.putExtra("day","Sunday");
                        startActivity(intent);
                        break;
                    }
                    default: break;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.timetableHomeBtn)
        {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public class SimpleAdapter extends BaseAdapter{

        private Context mContext;
        private LayoutInflater layoutInflater;
        private TextView days;
        private String[] dayArray;


        public SimpleAdapter(Context context, String[] days){
            mContext = context;
            dayArray = days;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return dayArray.length;
        }

        @Override
        public Object getItem(int i) {
            return dayArray[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = layoutInflater.inflate(R.layout.contact_button, null);
            }
            LinearLayout layout = convertView.findViewById(R.id.contact_button);
            layout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#cc" + getHexColor(i))));

            days = convertView.findViewById(R.id.contact_text);
            days.setText(dayArray[i]);

            return convertView;
        }
    }

    private String getHexColor(int index)
    {
        String[] colorHexes = getResources().getStringArray(R.array.colours);

        return colorHexes[index % colorHexes.length];
    }


}
