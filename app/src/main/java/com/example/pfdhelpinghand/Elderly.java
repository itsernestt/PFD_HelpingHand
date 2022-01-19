package com.example.pfdhelpinghand;


import java.util.ArrayList;
import java.util.List;

public class Elderly extends User{

    private String address;
    private String currentLocation;

    protected ArrayList<Medication> medList;
    protected ArrayList<Appointment> apptList;
    protected ArrayList<String> caretakerList;

    private ArrayList<EmergencyPerson> emergencyPersonList;

    public Elderly(){}

    public Elderly(String id, String name, String email, String phoneNum, String pw, String address, String cLocation, ArrayList<EmergencyPerson> eList,
                   ArrayList<Medication> mList, ArrayList<Appointment> aList, ArrayList<String> cList){
        this.ID = id;
        this.fullName = name;
        this.email = email;
        this.phoneNumber = phoneNum;
        this.password = pw;
        this.address = address;
        this.currentLocation = cLocation;
        this.emergencyPersonList = eList;
        this.medList = mList;
        this.apptList = aList;
        this.caretakerList = cList;
    }

    public String getAddress(){
        return address;
    }
    public void setElderlyAddress(String elderlyAddress) {

        this.address = elderlyAddress;
    }

    public String getCurrentLocation(){
        return currentLocation;
    }
    public void setCurrentLocation(String currentLocation) {

        this.currentLocation = currentLocation;
    }

    public ArrayList<EmergencyPerson> getEmergencyPerson(){
        return emergencyPersonList;
    }

    public void addEmergencyPerson(EmergencyPerson e){
        this.emergencyPersonList.add(e);
    }

    public ArrayList<Medication> getMedList() {

        return medList;
    }
    public void addMedication(Medication m){

        this.medList.add(m);
    }

    public ArrayList<Appointment> getApptList() {

        return apptList;
    }
    public void addAppointment(Appointment a){

        this.apptList.add(a);
    }

    public ArrayList<String> getCaretakerList(){
        return caretakerList;
    }

    public void addCaretaker(String c){
        this.caretakerList.add(c);
    }

}
