package itbstudentapp.com.itbstudentapp;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class Phone extends AppCompatActivity {

    private Button securityButt;
    private Button nurseButt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        securityButt = (Button) findViewById(R.id.buttonSecurity);
        nurseButt = (Button) findViewById(R.id.buttonNurse);


        securityButt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {


             /*   String Nurse ="018851105";
                String Counsellor = "018851321"; */


                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:018851099"));
/*
                If change to call the number straight away some issue with permissions become an issue maybe something we can look at

                if (ActivityCompat.checkSelfPermission(Phone.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                } */
                startActivity(callIntent);
            }
        });
        nurseButt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {


              String Counsellor = "018851321";


                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:018851105"));

                startActivity(callIntent);
            }
        });

    }
}
