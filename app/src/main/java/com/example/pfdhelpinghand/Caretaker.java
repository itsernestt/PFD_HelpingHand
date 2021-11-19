package com.example.pfdhelpinghand;


import java.util.ArrayList;
import java.util.List;

public class Caretaker extends User{
    // Elderly being taken care of by this caretaker
    protected List<Elderly> caringFor;

    public Caretaker(){

    }

    public Caretaker(String id, String name, String email, String phoneNum, String pw, ArrayList<Elderly> eList){
        this.ID = id;
        this.fullName = name;
        this.email = email;
        this.phoneNumber = phoneNum;
        this.password = pw;
        this.caringFor = eList;
    }


    public List<Elderly> returnElderly(){
        return caringFor;
    }

    public void assignElderly(Elderly e){
        this.caringFor.add(e);
        e.caretakerList.add(this);
    }


}
