package com.example.pfdhelpinghand;


import android.location.Address;
import android.location.Geocoder;

import java.util.ArrayList;
import java.util.List;

public class Elderly extends User{

    private String address;
    private String currentLocation;
    private Integer p_score;

    protected ArrayList<Medication> medList;
    protected ArrayList<Appointment> apptList;
    protected ArrayList<String> caretakerList;


    protected ArrayList<EmergencyPerson> emergencyPerson;

    public Elderly(){}

    public Elderly(String id, String name, String email, String phoneNum, String pw, Integer p, String address, String cLocation, ArrayList<EmergencyPerson> eList,
                   ArrayList<Medication> mList, ArrayList<Appointment> aList, ArrayList<String> cList){
        this.ID = id;
        this.fullName = name;
        this.email = email;
        this.phoneNumber = phoneNum;
        this.password = pw;
        this.address = address;
        this.currentLocation = cLocation;
        this.emergencyPerson = eList;
        this.medList = mList;
        this.apptList = aList;
        this.caretakerList = cList;
        this.p_score = p;

    }

    public Integer getP_score() {
        return p_score;
    }

    public void setP_score(Integer p_score) {
        this.p_score = p_score;
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
        return emergencyPerson;
    }

    public void addEmergencyPerson(EmergencyPerson e){
        this.emergencyPerson.add(e);
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

    public void reducePScore()
    {
        if (this.p_score >= 8)
        {
            this.p_score -= 8;
        }
        else{
            this.p_score = 0;
        }


    }

    public void increasePScore()
    {
        if (this.p_score <= 95)
        {
            this.p_score += 5;
        }
        else
        {
            this.p_score = 100;
        }

    }

    public Integer getLevelOfAlert()
    {
        if (this.p_score <= 100 && this.p_score > 80)
        {
            return 0;
        }
        else if (this.p_score <= 80 && this.p_score > 50)
        {
            return 1;
        }
        else if (this.p_score <= 50 && this.p_score > 20)
        {
            return 2;
        }
        else
        {
            return 3;
        }
    }

    public String getAlertMessage()
    {
        if (this.p_score <= 100 && this.p_score > 80)
        {
            return "High punctuality score! No need to worry,";
        }
        else if (this.p_score <= 80 && this.p_score > 50)
        {
            return "Consider dropping some messages for reminder.";
        }
        else if (this.p_score <= 50 && this.p_score > 20)
        {
            return "A phone call is needed to remind!";
        }
        else
        {
            return "Very insistent in taking medication!";
        }

    }






}
