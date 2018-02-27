package com.itbstudentapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class BusTimeReciever extends AsyncTask<String, Void,Void>{

    private OnThreadComplete threadComplete;
    private BusTimeInfo[] times;

    public BusTimeReciever(OnThreadComplete threadComplete)
    {
        this.threadComplete = threadComplete;
    }

    public BusTimeInfo[] getTimes()
    {
        return times;
    }

    @Override
    protected Void doInBackground(String... strings) {

        times = null;

        try {
            times = getMinutesLeft(strings[0], strings[1]); // gets the times from server, using stop numbr and route number
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void params) {
        super.onPostExecute(params);
        threadComplete.onThreadCompleteCall();
    }

    private BusTimeInfo[] getMinutesLeft(String route, String stop_id) throws Exception
   {
       HttpURLConnection connection; // http connection
       BusTimeInfo[] timesLeft = null;

       URL bus =
               new URL("https://data.dublinked.ie/cgi-bin/rtpi/realtimebusinformation?stopid=" + stop_id +"&routeid=" +  route +"&format=json");

       connection = (HttpURLConnection) bus.openConnection();
       connection.setRequestMethod("GET");
       connection.connect();
       BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

       String inputLine;

       while ((inputLine = in.readLine()) != null)
       {
           JSONObject obj = new JSONObject(inputLine); // breaking down json objects to get the info we need
           JSONArray res = obj.getJSONArray("results");

           timesLeft = new BusTimeInfo[res.length()];
           for(int i = 0; i < res.length(); i++)
           {
               JSONObject bus_time = res.getJSONObject(i);
               String due_time = bus_time.getString("duetime");
               String bus_dest = bus_time.getString("destination");

               timesLeft[i] = new BusTimeInfo(bus_dest, due_time);
           }
       }
       in.close(); // close buffer

       return timesLeft; // return the times
   }





}
