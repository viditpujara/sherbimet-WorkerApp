package com.sherbimet.worker.Model;

public class Pincode {

    String PincodeID,PincodeNumber;

    public Pincode(String pincodeID, String pincodeNumber) {
        PincodeID = pincodeID;
        PincodeNumber = pincodeNumber;
    }

    public Pincode() {
    }

    public String getPincodeID() {
        return PincodeID;
    }

    public void setPincodeID(String pincodeID) {
        PincodeID = pincodeID;
    }

    public String getPincodeNumber() {
        return PincodeNumber;
    }

    public void setPincodeNumber(String pincodeNumber) {
        PincodeNumber = pincodeNumber;
    }
}
