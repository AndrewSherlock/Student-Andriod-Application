package itbstudentapp.com.itbstudentapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by andrew on 01/12/2017.
 */

public class DublinBusRouteFinder extends AsyncTask<Void, Void,String[]>{

    private String[] stopIdsInArea = {"4890", "2468", "2269", "1820", "1825", "1819", "2960", "7025", "7026", "4747"};
    private String routes[];
    private  Context appContext;

    public DublinBusRouteFinder(Context appContext)
    {
        this.appContext = appContext;
    }

    @Override
    protected String[] doInBackground(Void... voids) {
        routes =  getAvailibleRouteNumbers();
        return routes;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        Toast.makeText(appContext, "Please Wait", Toast.LENGTH_SHORT);
    }


    public String[] getRoutes()
    {
        return  this.routes;
    }

    private String[] getAvailibleRouteNumbers()
    {
        ArrayList<String> availableRoutes = new ArrayList<String>();
        String[] routes = null;

        try
        {
            for(int i = 0; i < stopIdsInArea.length;i++)
            {
                String json = getJsonStringOfDetails(stopIdsInArea[i]);
                String[] routesArray = DecodeJson(json);
                checkForConflicts(availableRoutes, routesArray);
            }


            Object[] objects = availableRoutes.toArray();
            routes = new String[objects.length];

            for(int x = 0; x < objects.length; x++)
            {
                routes[x] = objects[x].toString();
            }

        } catch (IOException exception)
        {
            Log.e("Error getting routes", "getAvailibleRouteNumbers: Error finding results");
        }

        return  routes;
    }

    private ArrayList<String> checkForConflicts(ArrayList<String> list, String[] currentRoutes)
    {
        if(list.size() == 0)
        {
            for(int i = 0; i < currentRoutes.length; i++)
            {
                list.add(currentRoutes[i]);
            }
        }

        for(int i = 0; i < currentRoutes.length; i++)
        {
            boolean isFound = false;
            for(int x = 0; x < list.size(); x++)
            {
                if(currentRoutes[i].equals(list.get(x)))
                {
                    isFound = true;
                    break;
                }
            }

            if(!isFound)
            {
                list.add(currentRoutes[i]);
            }
        }
        return list;
    }

    private String getJsonStringOfDetails(String stopID) throws IOException
    {
        HttpURLConnection connection;
        URL bus = new URL("https://data.dublinked.ie/cgi-bin/rtpi/busstopinformation?stopid=" + stopID);
        connection = (HttpURLConnection) bus.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        // async. this causes a crash!
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine = in.readLine();

        if(in.readLine() != null)
        {
            Log.e("Reading error", "getJsonStringOfDetails: To much information, has the server changed the output?");
            return null;
        }

        in.close();
        return inputLine;
    }

    private String RemoveChars(String routes)
    {
        String s = "";
        for(int i = 0; i < routes.length(); i++)
        {
            if(routes.charAt(i) == '[' || routes.charAt(i) == ']' || routes.charAt(i) == '{' || routes.charAt(i) == '}' || routes.charAt(i) == '"')
            {
                continue;
            }
            s+= routes.charAt(i);
        }

        return s;
    }

    private String[] DecodeJson(String routeInformation)
    {
        String[] split = routeInformation.split(":");
        String routes = RemoveChars(split[21]);

        return routes.split(",");

    }

}
