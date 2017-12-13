package com.itbstudentapp;

/**
 * Created by andrew on 13/12/2017.
 */

public class User {

    public String username;
    public String password;
    public String courseID;
    public boolean staffUser;
    public String email;

    public User(String username, String password, String courseID, boolean staffUser, String email)
    {
        this.username = username;
        this.password = password;
        this.courseID = courseID;
        this.staffUser = staffUser;
        this.email = email;
    }

}
