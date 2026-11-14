package com.voidsyntax.coffeehub.model;

import com.voidsyntax.coffeehub.enums.Role;

public class Pelanggan extends User {
    private String noTelepon;
    private String alamat;

    public Pelanggan(int id, String nama, String email, String password,
                     String noTelepon, String alamat) {
        super(id, nama, email, password, Role.PELANGGAN);
        this.noTelepon = noTelepon;
        this.alamat = alamat;
    }

    public String getNoTelepon() {
        return noTelepon;
    }

    public void setNoTelepon(String noTelepon) {
        this.noTelepon = noTelepon;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    @Override
    public void tampilkanInfo() {
        System.out.println("=== INFORMASI PELANGGAN ===");
        super.tampilkanInfo();
        System.out.println("No. Telepon : " + noTelepon);
        System.out.println("Alamat      : " + alamat);
    }
}
