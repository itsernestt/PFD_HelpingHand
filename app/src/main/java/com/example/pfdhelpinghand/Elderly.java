package com.example.pfdhelpinghand;


import java.util.ArrayList;
import java.util.List;

public class Elderly extends User{

    private String elderlyAddress;

    protected List<Medication> medList;
    protected List<Appointment> apptList;
    protected List<Caretaker> caretakerList;

    private List<EmergencyPerson> emergencyPersonList;



    public Elderly(String id, String name, String email, String phoneNum, String pw, String address, List<EmergencyPerson> eList){
        this.ID = id;
        this.fullName = name;
        this.email = email;
        this.phoneNumber = phoneNum;
        this.password = pw;
        this.elderlyAddress = address;
        this.emergencyPersonList = eList;
    }

    public String getAddress(){
        return elderlyAddress;
    }
    public List<EmergencyPerson> getEmergencyPerson(){
        return emergencyPersonList;
    }

    public void addMedication(Medication m){
        this.medList.add(m);
    }

    public void addAppointment(Appointment a){
        this.apptList.add(a);
    }

    public void addEmergencyPerson(EmergencyPerson e){this.emergencyPersonList.add(e); }

    public void addCaretaker(Caretaker c){ this.caretakerList.add(c); }

}
