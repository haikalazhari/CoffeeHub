package com.voidsyntax.coffeehub.model;

import java.math.BigDecimal;

import com.voidsyntax.coffeehub.interfaces.Identifiable;

public class Product implements Identifiable {
    private int id;
    private String nama;
    private String deskripsi;
    private BigDecimal harga;
    private int stok;
    private String gambar;
    private boolean tersedia;
    private Category kategori;

    public Product(int id, String nama, String deskripsi,
                   BigDecimal harga, int stok, String gambar,
                   boolean tersedia, Category kategori) {
        this.id = id;
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.stok = stok;
        this.gambar = gambar;
        this.tersedia = tersedia;
        this.kategori = kategori;
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

    public BigDecimal getHarga() {
        return harga;
    }

    public void setHarga(BigDecimal harga) {
        this.harga = harga;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public boolean isTersedia() {
        return tersedia;
    }

    public void setTersedia(boolean tersedia) {
        this.tersedia = tersedia;
    }

    public Category getKategori() {
        return kategori;
    }

    public void setKategori(Category kategori) {
        this.kategori = kategori;
    }

    public void tampilkanInfo() {
        System.out.println("=== INFORMASI PRODUK ===");
        System.out.println("ID        : " + id);
        System.out.println("Nama      : " + nama);
        System.out.println("Deskripsi : " + deskripsi);
        System.out.println("Harga     : Rp" + harga);
        System.out.println("Stok      : " + stok);
        System.out.println("Tersedia  : " + tersedia);
        System.out.println("Kategori  : " +
                (kategori != null ? kategori.getNama() : "Tidak ada"));
    }
}
