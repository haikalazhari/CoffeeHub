package com.voidsyntax.coffeehub.model;

import com.voidsyntax.coffeehub.interfaces.Identifiable;

public class Category implements Identifiable {
    private int id;
    private String nama;
    private String deskripsi;

    public Category(int id, String nama, String deskripsi) {
        this.id = id;
        this.nama = nama;
        this.deskripsi = deskripsi;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public void tampilkanInfo() {
        System.out.println("=== INFORMASI KATEGORI ===");
        System.out.println("ID        : " + id);
        System.out.println("Nama      : " + nama);
        System.out.println("Deskripsi : " + deskripsi);
    }
}
