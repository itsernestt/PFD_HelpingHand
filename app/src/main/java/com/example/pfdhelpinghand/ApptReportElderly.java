package com.example.pfdhelpinghand;

import java.util.ArrayList;

public class ApptReportElderly {
    public String elderlyID;
    public String elderlyName;
    public ArrayList<Appointment> apptList;

    public ApptReportElderly(){}

    public ApptReportElderly(String e, String n, ArrayList<Appointment> aList){
        this.elderlyID = e;
        this.elderlyName = n;
        this.apptList = aList;
    }
}
