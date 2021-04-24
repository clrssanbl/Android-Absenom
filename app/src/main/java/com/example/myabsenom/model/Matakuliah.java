package com.example.myabsenom.model;

public class Matakuliah {
    private String matkul;
    private String namaDosen;

    public Matakuliah() {
    }

    public Matakuliah(String matkul, String namaDosen) {
        this.matkul = matkul;
        this.namaDosen = namaDosen;

    }
    public String getMatkul() {
        return matkul;
    }

    public void setMatkul(String matkul) {
        this.matkul = matkul;
    }

    public String getNamaDosen() {
        return namaDosen;
    }

    public void setNamaDosen(String namaDosen) {
        this.namaDosen = namaDosen;
    }
}
