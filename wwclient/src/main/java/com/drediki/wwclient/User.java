package com.drediki.wwclient;

/**
 * Created by DreDiki on 2017/3/7.
 */

public class User {
    private String userName;
    private String password;
    private String id;
    private float nearByDistance;
    private boolean self;
    private boolean online;
    private boolean guest;
//    private long lastactivetime;
    private Position currentPosition;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.userName = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCurrentPosition(Position currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void setGuest(boolean guest) {
        this.guest = guest;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public Position getLastPosition() {
        return currentPosition;
    }
}
