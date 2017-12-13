package com.itbstudentapp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class StopInformationFinder extends AsyncTask<String, Void,Stop[]> {

    private String[] stopIdsInArea = {"4890", "2468", "2269", "1820", "1825", "1819", "2960", "7025", "7026", "4747"};
    private ArrayList<Stop> stops;

    @Override
    protected Stop[] doInBackground(String... strings) {
        stops = new ArrayList<Stop>();

        try {
            GetRequest(strings[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Stop stopList[] = stops.toArray(new Stop[stops.size()]);
        return stopList;
    }

    private void GetRequest(String route) throws Exception
    {
        HttpURLConnection connection;

        URL bus =
                new URL("https://data.dublinked.ie/cgi-bin/rtpi/routeinformation?routeid="+ route +"&operator=bac&format=json");
        connection = (HttpURLConnection) bus.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String inputLine;

        while ((inputLine = in.readLine()) != null)
        {
            JSONObject obj = new JSONObject(inputLine);
            JSONArray arr = obj.getJSONArray("results");
            JSONObject ar = arr.getJSONObject(0);
            JSONArray test = ar.getJSONArray("stops");

            for(int i = 0; i < test.length(); i++)
            {
                JSONObject fields = test.getJSONObject(i);

                for(int x = 0; x < stopIdsInArea.length; x++)
                {
                    if(stopIdsInArea[x].equalsIgnoreCase(fields.getString("stopid")))
                    {
                        Stop stop = new Stop(fields.getString("fullname"), fields.getString("stopid"));
                        stops.add(stop);
                    }
                }
            }
        }

        in.close();

    }

}
