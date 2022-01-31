package com.example.pfdhelpinghand;

import java.util.ArrayList;

public class MedReportElderly {
    public String elderlyID;
    public String elderlyName;
    public ArrayList<Medication> alarmSuccess;
    public ArrayList<Medication> alarmFailed;

    public  MedReportElderly(){}

    public MedReportElderly(String id, String name, ArrayList<Medication> sList, ArrayList<Medication> fList){
        this.elderlyID = id;
        this.elderlyName = name;
        this.alarmSuccess = sList;
        this.alarmFailed = fList;
    }
}
