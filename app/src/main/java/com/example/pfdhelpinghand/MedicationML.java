package com.example.pfdhelpinghand;

import java.util.ArrayList;

public class MedicationML {
    private String elderlyID;
    private String elderlyName;

    private ArrayList<Medication> alarmFailed;
    private ArrayList<Medication> alarmSuccess;

    public MedicationML(){};

    public MedicationML(String id, String name, ArrayList<Medication> mFail, ArrayList<Medication> mSuccess)
    {
        this.elderlyID = id;
        this.elderlyName = name;
        this.alarmFailed = mFail;
        this.alarmSuccess = mSuccess;
    }


    public String getElderlyName() {
        return elderlyName;
    }

    public void setElderlyName(String elderlyName) {
        this.elderlyName = elderlyName;
    }

    public String getElderlyID() {
        return elderlyID;
    }

    public void setElderlyID(String elderlyID) {
        this.elderlyID = elderlyID;
    }

    public ArrayList<Medication> getAlarmFailed() {
        return alarmFailed;
    }

    public void setAlarmFailed(ArrayList<Medication> alarmFailed) {
        this.alarmFailed = alarmFailed;
    }

    public ArrayList<Medication> getAlarmSuccess() {
        return alarmSuccess;
    }

    public void setAlarmSuccess(ArrayList<Medication> alarmSuccess) {
        this.alarmSuccess = alarmSuccess;
    }
}
