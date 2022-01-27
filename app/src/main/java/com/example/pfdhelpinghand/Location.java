package com.example.pfdhelpinghand;

public class Location {
    public float lat;
    public float lng;
    public int time;


    public Location(){

    }

    public Location(float latitude, float longitude, int timeInSec)
    {
        this.lat = latitude;
        this.lng = longitude;
        this.time = timeInSec;
    }

    public float getLat() {
        return lat;
    }

    public float getLng() {
        return lng;
    }

    public int getTimeInSec() {
        return time;
    }





}
