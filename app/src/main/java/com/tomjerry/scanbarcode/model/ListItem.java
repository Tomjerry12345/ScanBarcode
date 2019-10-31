package com.tomjerry.scanbarcode.model;

public class ListItem {
    int id;
    String nama;
    String alamat;

    public ListItem(int id, String nama, String alamat) {
        this.id = id;
        this.nama = nama;
        this.alamat = alamat;
    }

    public int getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }


    public String getAlamat() {
        return alamat;
    }

}
