package itbstudentapp.com.itbstudentapp;

/**
 * Created by andrew on 11/12/2017.
 */

public class Stop {

    private String stop_name;
    private String stop_number;

    public  Stop(String stop_name, String stop_number)
    {
        this.stop_name = stop_name;
        this.stop_number = stop_number;
    }

    public String getStop_name() {
        return stop_name;
    }

    public String getStop_number() {
        return stop_number;
    }
}
