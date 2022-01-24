package com.example.pfdhelpinghand;

public class EmergencyPerson {

    protected String fullName;
    protected String phoneNumber;

    public EmergencyPerson(){
    }


    public EmergencyPerson(String name, String phone)
    {
        this.fullName = name;
        this.phoneNumber = phone;
    }


    public String getFullName() { return fullName; }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
