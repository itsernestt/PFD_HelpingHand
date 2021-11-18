package com.example.pfdhelpinghand;


import java.util.List;

public class Caretaker extends User{
    // Elderly being taken care of by this caretaker
    protected List<Elderly> caringFor;

    public List<Elderly> returnElderly(){
        return caringFor;
    }

    public void assignElderly(Elderly e){
        this.caringFor.add(e);
        e.caretakerList.add(this);
    }
}
