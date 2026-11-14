package com.voidsyntax.coffeehub.model;

import com.voidsyntax.coffeehub.enums.Role;

public class Admin extends User {
    private String jabatan;

    public Admin(int id, String nama, String email, String password, String jabatan) {
        super(id, nama, email, password, Role.ADMIN);
        this.jabatan = jabatan;
    }

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    @Override
    public void tampilkanInfo() {
        System.out.println("=== INFORMASI ADMIN ===");
        super.tampilkanInfo();
        System.out.println("Jabatan  : " + jabatan);
    }
}
