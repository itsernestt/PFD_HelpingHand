package com.example.pfdhelpinghand;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Caretaker extends User{
    // Elderly being taken care of by this caretaker
    protected ArrayList<String> elderlyList;

    public Caretaker(){}

    public Caretaker(String id, String name, String email, String phoneNum, String pw, ArrayList<String> eList){
        this.ID = id;
        this.fullName = name;
        this.email = email;
        this.phoneNumber = phoneNum;
        this.password = pw;
        this.elderlyList = eList;
    }

    public ArrayList<String> getElderlyList(){

        return elderlyList;
    }

    public void assignElderly(String eID){
        this.elderlyList.add(eID);
    }


}
