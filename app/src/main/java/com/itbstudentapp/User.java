package com.itbstudentapp;

/**
 * Created by andrew on 13/12/2017.
 */

public class User {

    private String username;
    private String password;
    private String courseID;
    private String accountType;
    private String email;
    private String imageLink;

    private boolean lockedAccount;

    public User(){}

    public User(String username, String password, String courseID, String accountType, String email, boolean lockedAccount) {
        this.username = username;
        this.password = password;
        this.courseID = courseID;
        this.accountType = accountType;
        this.email = email;
        this.lockedAccount = lockedAccount;
    }

    public User(String username, String password, String courseID, String accountType, String email, boolean lockedAccount, String imageLink) {
        this.username = username;
        this.password = password;
        this.courseID = courseID;
        this.accountType = accountType;
        this.email = email;
        this.lockedAccount = lockedAccount;
        this.imageLink = imageLink;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isLockedAccount() {
        return lockedAccount;
    }

    public void setLockedAccount(boolean lockedAccount) {
        this.lockedAccount = lockedAccount;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}