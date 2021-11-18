package com.example.pfdhelpinghand;

public class Medication {
    public String medName;
    public String medDescription;
    public String day;

    public Medication(){}

    public Medication(String name, String des, String day){
        this.medDescription = des;
        this.medName = name;
        this.day = day;
    }
}
