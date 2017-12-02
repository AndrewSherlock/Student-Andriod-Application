package itbstudentapp.com.itbstudentapp;



import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class Phone extends AppCompatActivity implements View.OnClickListener {

    private Button securityButt;
    private Button nurseButt;
    private Button CounsellorButt;
    private Button SidButt;
    private Button ServicesButt;
    private Button ExamButt;
    private Button BackButt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        securityButt = (Button) findViewById(R.id.buttonSecurity);
        nurseButt = (Button) findViewById(R.id.buttonNurse);
       CounsellorButt = (Button) findViewById(R.id.buttonCounsellor);
        SidButt = (Button) findViewById(R.id.buttonSid);
        ServicesButt = (Button) findViewById(R.id.buttonService);
        ExamButt =  (Button) findViewById(R.id.buttonExam);


        BackButt =  (Button) findViewById(R.id.home);
        BackButt.setOnClickListener(this);


        securityButt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:018851099"));
                startActivity(callIntent);
            }
        });
        nurseButt.setOnClickListener(new View.OnClickListener() {   // nurse
            public void onClick(View arg0) {


                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:018851105"));
                startActivity(callIntent);
            }
        });
        CounsellorButt.setOnClickListener(new View.OnClickListener() {  // counsellor Sandra Carrol
            public void onClick(View arg0) {

                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:018851105"));
                startActivity(callIntent);
            }
        });
        SidButt.setOnClickListener(new View.OnClickListener() {  // sid desk
            public void onClick(View arg0) {

                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:018851592"));
                startActivity(callIntent);
            }
        });
        ServicesButt.setOnClickListener(new View.OnClickListener() { // student services Sinead Dunne
            public void onClick(View arg0) {

                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:018851014"));
                startActivity(callIntent);
            }
        });
        ExamButt.setOnClickListener(new View.OnClickListener() { // student services Sinead Dunne
            public void onClick(View arg0) {

                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:018851042"));
                startActivity(callIntent);
            }
        });


    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }
}
