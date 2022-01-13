package com.example.pfdhelpinghand;

public class Appointment {
    public String apptName;
    public String location;
    public String day;

    public Appointment(){}

    public Appointment(String apptName, String location, String day) {
        this.apptName = apptName;
        this.location = location;
        this.day = day;
    }

    public String getDay(){
        return day;
    }




}
