package com.itbstudentapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.itbstudentapp.utils.LetterImageView;

import java.util.ArrayList;

public class DayView extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DayView";
    DatabaseHelper databaseHelper;
    private ListView listView;
    private Button addNewClass;
    private String selectedDay;
    private TextView dayViewing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_view);
        addNewClass = findViewById(R.id.addNewClass);
        addNewClass.setOnClickListener(this);

        dayViewing = findViewById(R.id.dayName);

        listView = findViewById(R.id.listCourseByDay);
        databaseHelper = new DatabaseHelper(this);
        Intent receivedIntent = getIntent();

        //now get the day we passed as an extra
        selectedDay = receivedIntent.getStringExtra("day");
        dayViewing.setText(selectedDay);
        populateListView();

     }

    private void populateListView() {
        Log.d(TAG, "populateListView: Displaying data in the ListView.");

        //get the data and append to a list

        Cursor data = databaseHelper.getDataByDay(selectedDay);
        ArrayList<String> listDataClassNames = new ArrayList<>();
        ArrayList<String> listDataClassTimes = new ArrayList<>();
        ArrayList<String> listDataClassRooms = new ArrayList<>();
        while(data.moveToNext()){
            //get the value from the database in column 2 - class name
            //then add it to the ArrayList
            listDataClassNames.add(data.getString(3));
            listDataClassTimes.add(data.getString(1)+ " - "+data.getString(2));
            listDataClassRooms.add(data.getString(5));
        }
        //create the list adapter and set the adapter

        DayAdapter adapter = new DayAdapter(this, R.layout.content_day_view, listDataClassNames, listDataClassTimes, listDataClassRooms);
        listView.setAdapter(adapter);

        //set an onItemClickListener to the ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String class_event = adapterView.getItemAtPosition(i).toString();
                Log.d(TAG, "onItemClick: You Clicked on " + class_event);

                Cursor data = databaseHelper.getItemID(class_event, selectedDay);//get the id associated with that name
                int itemID = -1;
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }
                if(itemID > -1){
                    Log.d(TAG, "onItemClick: The ID is: " + itemID);
                    Intent editScreenIntent = new Intent(DayView.this, EditTimetableEntryActivity.class);
                    editScreenIntent.putExtra("id",itemID);
                    editScreenIntent.putExtra("class/event",class_event);
                    editScreenIntent.putExtra("day",selectedDay);
                    startActivity(editScreenIntent);
                }
                else{
                    toastMessage("No ID associated with that name");
                }
            }
        });
    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }

    public void onClick(View view) {

        if(view.getId() == R.id.addNewClass)
        {
            Intent intent = new Intent(this, AddClass.class);
            intent.putExtra("day",selectedDay);
            startActivity(intent);
        }
    }

    public class DayAdapter extends ArrayAdapter{

        private int resource;
        private LayoutInflater layoutInflater;
        private String[] classes = new String[]{};
        private String[] classTimes = new String[]{};
        private String[] classRooms = new String[]{};

        public DayAdapter(Context context, int resource, ArrayList<String> classNames, ArrayList<String> listDataClassTimes, ArrayList<String> listDataClassRooms) {
            super(context, resource, classNames);
            this.resource = resource;
            this.classes = classNames.toArray(new String[0]);
            this.classTimes = listDataClassTimes.toArray(new String[0]);
            this.classRooms = listDataClassRooms.toArray(new String[0]);
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
            viewHolder.classTimeText.setText(classTimes[position]);
            viewHolder.classRoomText.setText(classRooms[position]);

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
