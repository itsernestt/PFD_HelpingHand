package com.example.pfdhelpinghand;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Caretaker extends User{
    // Elderly being taken care of by this caretaker
    protected ArrayList<Elderly> elderlyList;

    public Caretaker(){}

    public Caretaker(String id, String name, String email, String phoneNum, String pw, ArrayList<Elderly> eList){
        this.ID = id;
        this.fullName = name;
        this.email = email;
        this.phoneNumber = phoneNum;
        this.password = pw;
        this.elderlyList = eList;
    }

    public ArrayList<Elderly> getElderlyList(){

        return elderlyList;
    }

    public void assignElderly(Elderly e){
        this.elderlyList.add(e);
    }


}
