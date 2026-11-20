package com.voidsyntax.coffeehub.model;

import com.voidsyntax.coffeehub.enums.Role;
import com.voidsyntax.coffeehub.interfaces.Identifiable;

public abstract class User implements Identifiable {
    private int id;
    private String nama;
    private String email;
    private String password;
    private Role role;
    private String noTelepon;

    public User(int id, String nama, String email, String password, Role role) {
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.password = password;
        this.role = role;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getNoTelepon() {
        return noTelepon;
    }

    public void setNoTelepon(String noTelepon) {
        this.noTelepon = noTelepon;
    }

    public void tampilkanInfo() {
        System.out.println("ID       : " + id);
        System.out.println("Nama     : " + nama);
        System.out.println("Email    : " + email);
        System.out.println("Role     : " + role);
    }
}
