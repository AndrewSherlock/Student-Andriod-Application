package itbstudentapp.com.itbstudentapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class Transport extends AppCompatActivity implements View.OnClickListener{

    private RelativeLayout dub_bus, bus_eir, irish_rail, itb_shuttle;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);

        dub_bus = (RelativeLayout) findViewById(R.id.dub_bus);
        bus_eir = (RelativeLayout) findViewById(R.id.bus_eir);
        irish_rail = (RelativeLayout) findViewById(R.id.iar_eir);
        itb_shuttle = (RelativeLayout) findViewById(R.id.shut_bus);

        dub_bus.setOnClickListener(this);
        bus_eir.setOnClickListener(this);
        irish_rail.setOnClickListener(this);
        itb_shuttle.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.dub_bus:
                Toast.makeText(getApplicationContext(), "Getting available routes.", Toast.LENGTH_SHORT);
                CallBusScreen("dublin_bus");
                break;
            case R.id.bus_eir:
                break;
            case R.id.iar_eir:
                break;
            case R.id.shut_bus:
                break;
            default:
                Log.e("Error", "onClick: was unknown button.");
                break;
        }
    }

    private void CallBusScreen(String methodOfTravel)
    {
        // must use a thread here.
        String[] routes = null;

        if(methodOfTravel == "dublin_bus")
        {
            DublinBusRouteFinder dbhandler = new DublinBusRouteFinder(getApplicationContext());
            try {
                routes = dbhandler.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(this, RouteChoice.class);
        intent.putExtra("routes", routes);
        startActivity(intent);
    }
}
