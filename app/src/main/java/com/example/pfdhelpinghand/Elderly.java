package com.example.pfdhelpinghand;


import java.util.List;

public class Elderly extends User{

    protected List<Medication> medList;
    protected List<Appointment> apptList;
    protected List<Caretaker> caretakerList;

    public void addMedication(Medication m){
        this.medList.add(m);
    }

    public void addAppointment(Appointment a){
        this.apptList.add(a);
    }



}
