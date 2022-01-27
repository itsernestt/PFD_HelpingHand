package com.example.pfdhelpinghand;

public class ElderlyLocation {
    public double lat;
    public double lng;
    public int time;


    public ElderlyLocation(){

    }

    public ElderlyLocation(double latitude, double longitude, int timeInSec)
    {
        this.lat = latitude;
        this.lng = longitude;
        this.time = timeInSec;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
