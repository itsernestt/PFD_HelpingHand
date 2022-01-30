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

    public Integer getLevelOfAlert(Integer p_value)
    {
        if (p_value <= 100 && p_value > 80)
        {
            return 0;
        }
        else if (p_value <= 80 && p_value > 50)
        {
            return 1;
        }
        else if (p_value <= 50 && p_value > 20)
        {
            return 2;
        }
        else
        {
            return 3;
        }
    }


}
