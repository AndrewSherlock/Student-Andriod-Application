package itbstudentapp.com.itbstudentapp;

//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.widget.Button;
//import android.content.Intent;
//import android.view.View;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.Manifest;
//import android.support.v4.app.ActivityCompat;

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

    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        button = (Button) findViewById(R.id.buttonSecurity);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                String Security = " 018851099";
                String Nurse ="018851105";
                String Counsellor = "018851321";


                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+ Security));

                if (ActivityCompat.checkSelfPermission(Phone.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);
            }
        });

    }
}
