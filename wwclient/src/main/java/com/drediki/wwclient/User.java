package com.drediki.wwclient;

/**
 * User Information
 * @author DreDiki
 * @see Position
 * @version 1.0
 */
public class User {
    private String userName;
    private String password;
    private String id;
    private float nearByDistance = 1000;//default
    private boolean online =false;
    private boolean guest = true;
//    private long lastactivetime;
    private Position currentPosition;

    /**
     * Empty constructor.
     * You could use
     * @see #setName(String)
     * @see #setPassword(String)
     * @see #setGuest(boolean)
     * @see #setCurrentPosition(Position)
     * @see #setNearByDistance(float)
     * before login
     */
    public User(String username){
        this.userName = username;
    }

    /**
     * @param userName User Name
     * @param password Password (Can be null)
     * @param guest if the account is a guest account,the server will delete user information after logout
     */
    public User(String userName,String password,boolean guest){
        this.userName = userName;
        this.password = password;
        this.guest = guest;
    }

    /**
     * Do not change it,which is used to find other clients.
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * You cannot change it dynamically after login,maybe will support later.
     * @param name User Name To Set.
     */
    public void setName(String name) {
        this.userName = name;
    }

    /**
     * Set user's password
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Set user's position.
     * Only works before login.
     * After user logged in,
     * You should use {@link Client#update(Position)} instead.
     * @see Position
     * @param currentPosition where is this guy now.
     */
    public void setCurrentPosition(Position currentPosition) {
        this.currentPosition = currentPosition;
    }

    /**
     * How far the "Near By" is.
     * Length Unit:meter
     * default value:1000m
     * @param nearByDistance
     */
    public void setNearByDistance(float nearByDistance) {
        this.nearByDistance = nearByDistance;
    }

    /**
     * If the account is a guest account,the server will delete user information after logout
     * @param guest
     */
    public void setGuest(boolean guest) {
        this.guest = guest;
    }

    /**
     *
     * @return id used to identify the {@link Client} link to this User.
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @return User Name
     */
    public String getName() {
        return userName;
    }

    /**
     * The account information you get from the server will not tell you this.
     * @return User Password
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @return where is this guy now.
     */
    public Position getCurrentPosition() {
        return currentPosition;
    }
}
