package com.example.pfdhelpinghand;
import java.util.ArrayList;
import java.util.List;

public class PairUpRequest {

    private String documentID;
    private String senderEmail;
    private String receiverEmail;
    private Boolean isPairUpSuccess;

    public PairUpRequest(){}

    public PairUpRequest(String ID, String sEmail, String rEmail, Boolean p)
    {
        this.documentID = ID;
        this.senderEmail = sEmail;
        this.receiverEmail = rEmail;
        this.isPairUpSuccess = p;
    }


    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public Boolean getPairUpSuccess() {
        return isPairUpSuccess;
    }

    public void setPairUpSuccess(Boolean pairUpSuccess) {
        isPairUpSuccess = pairUpSuccess;
    }
}
