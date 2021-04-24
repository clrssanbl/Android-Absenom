package com.example.myabsenom.model;

public class Kehadiran {
    private String nama;
    private String nim;
    private String matkul;
    private String kehadiran;
    private String alasan_tdkhdr;
    private String tanggal;

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getMatkul() {
        return matkul;
    }

    public void setMatkul(String matkul) {
        this.matkul = matkul;
    }

    public String getKehadiran() {
        return kehadiran;
    }

    public void setKehadiran(String kehadiran) {
        this.kehadiran = kehadiran;
    }

    public String getAlasan_tdkhdr() {
        return alasan_tdkhdr;
    }

    public void setAlasan_tdkhdr(String alasan_tdkhdr) {
        this.alasan_tdkhdr = alasan_tdkhdr;
    }

    public Kehadiran(String nama, String nim, String matkul, String kehadiran, String tanggal) {
        this.nama = nama;
        this.nim = nim;
        this.matkul = matkul;
        this.kehadiran = kehadiran;
        this.tanggal = tanggal;
    }

    public Kehadiran(String nama, String nim, String matkul, String kehadiran, String tanggal,String alasan_tdkhdr) {
        this.nama = nama;
        this.nim = nim;
        this.matkul = matkul;
        this.kehadiran = kehadiran;
        this.alasan_tdkhdr = alasan_tdkhdr;
        this.tanggal = tanggal;
    }
}
