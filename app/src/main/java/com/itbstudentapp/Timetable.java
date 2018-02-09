package com.itbstudentapp;

import android.content.Context;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Random;

public class Timetable extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                        startActivity(intent);
                        break;
                    }
                    case 1:{

                        break;
                    }
                    case 2:{

                        break;
                    }
                    case 3:{

                        break;
                    }
                    case 4:{

                        break;
                    }
                    default: break;
                }
            }
        });
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
                convertView = layoutInflater.inflate(R.layout.activity_timetable_single_item, null);
            }
            days = convertView.findViewById(R.id.weekDay);
            days.setText(dayArray[i]);
            String colour = nextColour(i);
            convertView.setBackgroundColor(Color.parseColor(colour));
            return convertView;
        }
    }

    public String nextColour(int i){
        String [] colorArray = getResources().getStringArray(R.array.colours);
        return colorArray[i];
    }


}
