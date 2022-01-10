package com.example.pfdhelpinghand;


import java.util.ArrayList;
import java.util.List;

public class Elderly extends User{

    private String elderlyAddress;

    protected ArrayList<Medication> medList;
    protected ArrayList<Appointment> apptList;
    protected ArrayList<String> caretakerList;

    private List<EmergencyPerson> emergencyPersonList;

    public Elderly(){}

    public Elderly(String id, String name, String email, String phoneNum, String pw, String address, List<EmergencyPerson> eList,
                   ArrayList<Medication> mList, ArrayList<Appointment> aList, ArrayList<String> cList){
        this.ID = id;
        this.fullName = name;
        this.email = email;
        this.phoneNumber = phoneNum;
        this.password = pw;
        this.elderlyAddress = address;
        this.emergencyPersonList = eList;
        this.medList = mList;
        this.apptList = aList;
        this.caretakerList = cList;
    }

    public String getAddress(){
        return elderlyAddress;
    }
    public void setElderlyAddress(String elderlyAddress) {

        this.elderlyAddress = elderlyAddress;
    }

    public List<EmergencyPerson> getEmergencyPerson(){
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
