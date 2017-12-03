package itbstudentapp.com.itbstudentapp;

import android.os.AsyncTask;

/**
 * Created by andrew on 03/12/2017.
 */

public class DublinBusRouteInformation extends AsyncTask<String, Void, String[]>
{
    // "https://data.dublinked.ie/cgi-bin/rtpi/routeinformation?routeid=70&operator=bac&format=json" <= this wont be fun
    public DublinBusRouteInformation(){}

    @Override
    protected String[] doInBackground(String... strings) {
        return new String[0];
    }

}
