package com.example.pfdhelpinghand;


import java.util.ArrayList;
import java.util.List;

public class Elderly extends User{

    private String elderlyAddress;

    protected ArrayList<Medication> medList;
    protected ArrayList<Appointment> apptList;
    protected ArrayList<Caretaker> caretakerList;

    private List<EmergencyPerson> emergencyPersonList;

    public Elderly(){}

    public Elderly(String id, String name, String email, String phoneNum, String pw, String address, List<EmergencyPerson> eList,
                   ArrayList<Medication> mList, ArrayList<Appointment> aList){
        this.ID = id;
        this.fullName = name;
        this.email = email;
        this.phoneNumber = phoneNum;
        this.password = pw;
        this.elderlyAddress = address;
        this.emergencyPersonList = eList;
        this.medList = mList;
        this.apptList = aList;
    }

    public void setElderlyAddress(String elderlyAddress) {
        this.elderlyAddress = elderlyAddress;
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

    public ArrayList<Appointment> getApptList() {
        return apptList;
    }

    public ArrayList<Medication> getMedList() {
        return medList;
    }

    public void addEmergencyPerson(EmergencyPerson e){this.emergencyPersonList.add(e); }

    public void addCaretaker(Caretaker c){ this.caretakerList.add(c); }

}
