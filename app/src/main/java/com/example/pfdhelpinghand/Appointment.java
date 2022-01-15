package com.example.pfdhelpinghand;

import java.sql.Timestamp;

public class Appointment implements Comparable<Appointment>{
    public String apptName;
    public String location;
    public com.google.firebase.Timestamp time;

    public Appointment(){}

    public Appointment(String apptName, String location, com.google.firebase.Timestamp time) {
        this.apptName = apptName;
        this.location = location;
        this.time = time;
    }

    public com.google.firebase.Timestamp getTime(){
        return time;
    }

    @Override
    public int compareTo(Appointment o) {
        return getTime().compareTo(o.getTime());
    }
}