<div align="center">

  <img src="src/main/resources/images/coffeehub-logo.png" alt="CoffeeHub Logo" width="180">

  # CoffeeHub

  **Sistem Informasi Pemesanan dan Manajemen Kedai Kopi Berbasis Desktop**

  Aplikasi desktop untuk mengelola operasional kedai kopi, mulai dari produk dan persediaan hingga pemesanan pelanggan.

  <br>

  ![Java](https://img.shields.io/badge/Java-26-ED8B00?logo=openjdk&logoColor=white)
  ![JavaFX](https://img.shields.io/badge/JavaFX-27.0.1-2F6B9A)
  ![Maven](https://img.shields.io/badge/Maven-3.10.0-C71A36?logo=apachemaven&logoColor=white)
  ![MariaDB](https://img.shields.io/badge/MariaDB-Database-003545?logo=mariadb&logoColor=white)

  **VoidSyntax · Kelompok 13**

</div>

---

## Daftar Isi

- [Tentang Proyek](#tentang-proyek)
- [Fitur](#fitur)
- [Teknologi](#teknologi)
- [Persyaratan Sistem](#persyaratan-sistem)
- [Struktur Proyek](#struktur-proyek)
- [Persiapan Database](#persiapan-database)
- [Konfigurasi Aplikasi](#konfigurasi-aplikasi)
- [Menjalankan Aplikasi](#menjalankan-aplikasi)
- [Build](#build)
- [Tim Pengembang](#tim-pengembang)

## Tentang Proyek

CoffeeHub adalah aplikasi desktop yang dikembangkan untuk membantu pengelolaan kedai kopi secara terintegrasi. Aplikasi menyediakan dua jenis akses, yaitu **Admin** dan **Pelanggan**.

Admin dapat mengelola data produk, kategori, stok, dan pesanan. Pelanggan dapat melihat katalog produk, memasukkan produk ke keranjang, melakukan checkout dengan pembayaran tunai, serta melihat riwayat pesanan.

## Fitur

### Admin

- **Dashboard** — Ringkasan data produk, kategori, dan pesanan.
- **Product Management** — Menambah, melihat, mengubah, dan menghapus produk.
- **Category Management** — Mengelola kategori produk.
- **Stock Management** — Memantau dan memperbarui persediaan produk.
- **Order Management** — Melihat dan mengelola pesanan pelanggan.
- **Cash Payment Management** — Mencatat dan memperbarui status pembayaran tunai.

### Pelanggan

- **Customer Dashboard** — Ringkasan informasi produk.
- **Product Catalog** — Melihat produk yang tersedia.
- **Shopping Cart** — Mengelola produk yang akan dipesan.
- **Cash Checkout** — Membuat pesanan dengan metode pembayaran tunai.
- **Order History** — Melihat riwayat dan status pesanan.

## Teknologi

| Komponen | Teknologi |
|---|---|
| Bahasa pemrograman | Java 26 |
| GUI Framework | JavaFX 27.0.1 |
| Layout antarmuka | FXML |
| Styling | JavaFX CSS |
| Build tool | Apache Maven 3.10.0 |
| Database | MariaDB |
| Koneksi database | JDBC |
| IDE yang digunakan | Visual Studio Code |

## Persyaratan Sistem

Pastikan perangkat telah memiliki:

- JDK 26
- Apache Maven 3.10.0
- MariaDB Server
- Git

Periksa instalasi Java dan Maven melalui terminal:

```bash
java -version
mvn -version
git --version
```

## Struktur Proyek

Struktur utama repository:

```text
CoffeeHub/
├── database/
│   └── coffeehub.sql
├── src/
│   ├── main/
│   │   ├── java/com/voidsyntax/coffeehub/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── enums/
│   │   │   ├── interfaces/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── Main.java
│   │   └── resources/
│   │       ├── css/
│   │       ├── fxml/
│   │       └── images/
│   └── test/
│       └── java/
├── .gitignore
├── pom.xml
└── README.md
```

**Ringkasan direktori:**

| Direktori/File | Keterangan |
|---|---|
| `database/` | Berisi file SQL untuk menyiapkan database. |
| `config/` | Konfigurasi koneksi database. |
| `controller/` | Controller untuk halaman dan interaksi aplikasi. |
| `enums/` | Enumerasi untuk role, status pesanan, dan pembayaran. |
| `interfaces/` | Interface yang digunakan dalam project. |
| `model/` | Class model untuk entitas aplikasi. |
| `repository/` | Akses dan operasi data melalui DAO. |
| `service/` | Logika layanan aplikasi. |
| `resources/css/` | File stylesheet JavaFX. |
| `resources/fxml/` | Layout antarmuka aplikasi. |
| `resources/images/` | Logo dan ikon aplikasi. |

## Persiapan Database

1. Jalankan MariaDB Server.
2. Buat database bernama `coffeehub`.
3. Import file `database/coffeehub.sql` melalui phpMyAdmin atau klien MariaDB.
4. Pastikan database berhasil dibuat sebelum menjalankan aplikasi.

Contoh membuat database melalui SQL:

```sql
CREATE DATABASE coffeehub;
```

## Konfigurasi Aplikasi

CoffeeHub menggunakan file lokal `database.properties` untuk menyimpan konfigurasi koneksi database.

Buat file bernama `database.properties` di **direktori utama proyek** (sejajar dengan `pom.xml`), lalu isi sesuai konfigurasi MariaDB di perangkat:

```properties
db.url=jdbc:mariadb://127.0.0.1:3306/coffeehub?sslMode=disable
db.username=root
db.password=
```

Sesuaikan URL, username, dan password jika konfigurasi database berbeda.

> **Catatan keamanan:** `database.properties` berisi konfigurasi lokal dan tidak boleh di-commit ke repository. Pastikan file tersebut tercantum di `.gitignore`. Jangan mengunggah password atau kredensial database ke GitHub.

## Menjalankan Aplikasi

Clone repository:

```bash
git clone <URL_REPOSITORY_GITHUB>
```

Masuk ke direktori proyek:

```bash
cd CoffeeHub
```

Setelah database dan `database.properties` siap, jalankan aplikasi:

```bash
mvn javafx:run
```

## Build

Untuk membersihkan hasil build sebelumnya dan melakukan kompilasi:

```bash
mvn clean compile
```

## Tim Pengembang

| Informasi | Detail |
|---|---|
| Nama proyek | CoffeeHub |
| Kelompok | VoidSyntax |
| Nomor kelompok | 13 |
| Jenis aplikasi | Desktop Application |
| Program studi | Teknik Informatika |

---

<div align="center">

  **CoffeeHub — Simplifying Coffee Shop Management**

  *Developed by VoidSyntax · Group 13*

</div>
