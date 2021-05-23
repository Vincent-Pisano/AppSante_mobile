package com.renaud.appsante.model;

import com.parse.ParseUser;

import java.time.LocalDate;

public class Permit {

    public static final String TYPE_VACCIN = "VACCIN";
    public static final String TYPE_TEST = "TEST";

    private String type;
    private Integer nbrDose;
    private LocalDate dateTest;
    private LocalDate dateExpiration;
    private String NAS;
    private byte[] qrCode;
    private boolean isActive;

    public Permit() {
        this.isActive = true;
    }

    public Permit(String type, Integer nbrDose, LocalDate dateTest, LocalDate dateExpiration, String NAS, boolean isActive) {
        this.type = type;
        this.nbrDose = nbrDose;
        this.dateTest = dateTest;
        this.dateExpiration = dateExpiration;
        this.NAS = NAS;
        this.isActive = isActive;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getNbrDose() {
        return nbrDose;
    }

    public void setNbrDose(Integer nbrDose) {
        this.nbrDose = nbrDose;
    }

    public LocalDate getDateTest() {
        return dateTest;
    }

    public void setDateTest(LocalDate dateTest) {
        this.dateTest = dateTest;
    }

    public LocalDate getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public String getNAS() {
        return NAS;
    }

    public void setNAS(String NAS) {
        this.NAS = NAS;
    }

    public byte[] getQrCode() {
        return qrCode;
    }

    public void setQrCode(byte[] qrCode) {
        this.qrCode = qrCode;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "Permit{" +
                "type='" + type + '\'' +
                ", nbrDose=" + nbrDose +
                ", dateTest=" + dateTest +
                ", dateExpiration=" + dateExpiration +
                ", citizen=" + NAS +
                ", isActive=" + isActive +
                '}';
    }

    public String toQrData() {
        return ParseUser.getCurrentUser().get("NAS") + ";" +
                dateExpiration + ";" +
                ParseUser.getCurrentUser().get("phone");
    }

}
