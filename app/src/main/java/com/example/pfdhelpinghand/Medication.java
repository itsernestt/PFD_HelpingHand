package com.example.pfdhelpinghand;

public class Medication {
    public String medName;
    public String medDescription;
    public String day;
    public boolean weekly;

    public Medication(){}

    public Medication(String name, String des, String day, Boolean weekly){
        this.medDescription = des;
        this.medName = name;
        this.day = day;
        this.weekly = weekly;
    }

    public String getDay(){
        return this.day;
    }
}
