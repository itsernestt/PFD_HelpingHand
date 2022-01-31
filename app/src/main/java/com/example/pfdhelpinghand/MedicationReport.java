package com.example.pfdhelpinghand;

public class MedicationReport {
    public String medName;
    public int totalNumber;
    public int numSucceed;
    public String medDes;

    public MedicationReport(String name, int num, int suc, String des){
        this.medName = name;
        this.totalNumber = num;
        this.numSucceed = suc;
        this.medDes = des;
    }
}
