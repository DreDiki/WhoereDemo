package com.drediki.wwclient;

/**
 * Created by DreDiki on 2017/3/7.
 */

public class Position {
    public double latitude;
    public double longitude;
    public double accuracy;
    public long recordTime;
    public Position(double longitude,double latitude){
        this(longitude,latitude,-1,System.currentTimeMillis());
    }
    public Position(double longitude,double latitude,double accuracy,long recordTime){
        this.longitude = longitude;
        this.latitude = latitude;
        this.accuracy = accuracy;
        this.recordTime = recordTime;
    }
    public void set(double longitude,double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
