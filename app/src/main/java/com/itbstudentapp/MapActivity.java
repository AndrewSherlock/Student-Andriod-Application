package com.itbstudentapp;

import android.*;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, AdapterView.OnItemSelectedListener {

    private TextView addRoomButton, submit;
    private EditText enterRoomBox;
    private Spinner roomSpinner;

    private GoogleMap map;
    private boolean hasEnteredTextLast = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_map);

        mapFragment.getMapAsync(this);

        submit = findViewById(R.id.map_submit);
        enterRoomBox = findViewById(R.id.room_choice);
        roomSpinner = findViewById(R.id.room_drop_down);
        addRoomButton = findViewById(R.id.add_room);

        enterRoomBox.setOnClickListener(this);
        roomSpinner.setOnItemSelectedListener(this);

        submit.setOnClickListener(this);
        addRoomButton.setOnClickListener(this);

        addToSpinner();

    }

    private void addToSpinner()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("map_locations");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               String[] roomList = new String[(int)dataSnapshot.getChildrenCount()];
               int counter = 0;

               for(DataSnapshot data : dataSnapshot.getChildren())
               {
                   roomList[counter] = formatText(data.getKey());
                   counter++;
               }

               ArrayAdapter<String> rooms = new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_text, roomList);
               roomSpinner.setAdapter(rooms);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        map = googleMap;
        LatLng lat = new LatLng(53.4048029,-6.3791624);
        googleMap.setMinZoomPreference(15);
        googleMap.setMaxZoomPreference(25);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(lat));

        if(UtilityFunctions.askForLocationPermission(this))
        {
            googleMap.setMyLocationEnabled(true);
        } else{
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == enterRoomBox.getId())
        {
            hasEnteredTextLast = true;
            return;
        }

        if(!UtilityFunctions.doesUserHaveConnection(getBaseContext()))
        {
            Toast.makeText(getBaseContext(), "Please wait for network connection", Toast.LENGTH_SHORT).show();
            return;
        }

        if(v.getId() == addRoomButton.getId())
        {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.map_add_room_dialog);

            final EditText roomId = dialog.findViewById(R.id.add_location_text);
            TextView addButton = dialog.findViewById(R.id.add_location_button);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(roomId.length() <= 0)
                    {
                        Toast.makeText(getBaseContext(), "Please enter a name for location", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(addRoomToDatabase(roomId.getText().toString()))
                    {
                        dialog.dismiss();
                        startActivity(getIntent());
                    }
                }
            });

            dialog.show();
        } else{
            String room;
            if(hasEnteredTextLast)
            {
                room = enterRoomBox.getText().toString();
            } else{
                room = roomSpinner.getSelectedItem().toString();
            }

            room = room.replace(" ", "_");
            room = room.trim();
            getCoords(room);

        }
    }

    private boolean addRoomToDatabase(String roomId)
    {
        Location location = null;

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestAccessLocationPermission();
        }

        String location_context = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) getBaseContext().getSystemService(location_context);

        for (String provider : locationManager.getProviders(true))
        {
            locationManager.requestLocationUpdates(provider, 1000, 0,
                    new LocationListener() {

                        public void onLocationChanged(Location location) {}

                        public void onProviderDisabled(String provider) {}

                        public void onProviderEnabled(String provider) {}

                        public void onStatusChanged(String provider, int status,
                                                    Bundle extras) {}
                    });
            location = locationManager.getLastKnownLocation(provider);
        }

        if(location != null)
        {
            roomId = roomId.replace(" ", "_");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("map_locations");
            reference.child(roomId.toLowerCase()).child("long").setValue(location.getLongitude());
            reference.child(roomId.toLowerCase()).child("lat").setValue(location.getLatitude());
            return true;
        }

        return false;
    }

    private String formatText(String room)
    {
        String underScoreCheck = room.replace("_", " ");
        String capital = underScoreCheck.substring(0,1).toUpperCase() + underScoreCheck.substring(1, underScoreCheck.length());
        return  capital;
    }

    private void getCoords(final String room)
    {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("map_locations/");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(room.toLowerCase()).exists())
                {
                    double longatude = dataSnapshot.child(room.toLowerCase()).child("long").getValue(double.class);
                    double lat = dataSnapshot.child(room.toLowerCase()).child("lat").getValue(double.class);

                    setMapToPosition(longatude, lat, room);
                    return;
                } else {
                    Toast.makeText(getBaseContext(), "No such room. Please check again.", Toast.LENGTH_SHORT).show();
                    return;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setMapToPosition(double longatude, double lat, String room)
    {

        LatLng coord = new LatLng(lat,longatude);


        map.setMinZoomPreference(15);
        map.setMaxZoomPreference(25);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coord, 20));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(coord);
        markerOptions.title(room);
        map.addMarker(markerOptions);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        hasEnteredTextLast = false;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION=1;

    private void requestAccessLocationPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else
                {
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
