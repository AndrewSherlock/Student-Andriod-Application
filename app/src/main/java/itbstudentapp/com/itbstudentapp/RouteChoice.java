package itbstudentapp.com.itbstudentapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class RouteChoice extends AppCompatActivity{

    private String travel;
    private String[] routes;

    private LinearLayout linearLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_route_choice);
        linearLayout = (LinearLayout) findViewById(R.id.butt_screen);
        setUpPage();
    }

    private void setUpPage()
    {
        Bundle typeOfTravel = getIntent().getExtras();
        String[] routes = typeOfTravel.getStringArray("routes");

        for(String route : routes)
        {
            drawButtons(route);
        }
    }

    private void drawButtons(String route)
    {
        final Button routeButton = new Button(this);
        routeButton.setText(route);

        LinearLayout.LayoutParams linear = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        routeButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.e("Button number", "onClick: " + routeButton.getText());


            }
        });

        ((LinearLayout)linearLayout).addView(routeButton);
    }

    private void GetInformationFromAPI(String route)
    {

    }


}
