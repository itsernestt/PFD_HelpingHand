package com.example.pfdhelpinghand;

public class MedicationReport {
    public String medName;
    public int totalNumber;
    public int numSucceed;

    public MedicationReport(String name, int num, int suc){
        this.medName = name;
        this.totalNumber = num;
        this.numSucceed = suc;
    }
}
