package com.example.pfdhelpinghand;

import com.google.firebase.Timestamp;

public class Medication implements Comparable<Medication>{
    public String medName;
    public String medDescription;
    public Timestamp day;

    public Medication(){}

    public Medication(String name, String des, Timestamp day){
        this.medDescription = des;
        this.medName = name;
        this.day = day;
    }

    public Timestamp getDay(){
        return day;
    }

    @Override
    public int compareTo(Medication m){
        return getDay().compareTo(m.getDay());
    }
}
