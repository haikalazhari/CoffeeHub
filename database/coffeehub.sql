-- =====================================================
-- CoffeeHub Database Schema
-- Group: VoidSyntax
-- Group Number: 13
-- Database: MariaDB
-- =====================================================

CREATE DATABASE IF NOT EXISTS coffeehub
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE coffeehub;

-- =====================================================
-- 1. TABLE: users
-- =====================================================

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    no_telepon VARCHAR(20) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'PELANGGAN')
        NOT NULL DEFAULT 'PELANGGAN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =====================================================
-- 2. TABLE: categories
-- =====================================================

CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT
) ENGINE=InnoDB;

-- =====================================================
-- 3. TABLE: products
-- =====================================================

CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(12,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    image_path VARCHAR(255),
    is_available BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_products_categories
        FOREIGN KEY (category_id)
        REFERENCES categories(id)
        ON UPDATE CASCADE
        ON DELETE SET NULL
) ENGINE=InnoDB;

-- =====================================================
-- 4. TABLE: orders
-- =====================================================

CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_code VARCHAR(50) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    order_date DATETIME NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL,

    payment_method ENUM('TUNAI')
        NOT NULL DEFAULT 'TUNAI',

    payment_status ENUM('BELUM_BAYAR', 'LUNAS')
        NOT NULL DEFAULT 'BELUM_BAYAR',

    order_status ENUM(
        'MENUNGGU',
        'DIPROSES',
        'SIAP',
        'SELESAI',
        'DIBATALKAN'
    ) NOT NULL DEFAULT 'MENUNGGU',

    CONSTRAINT fk_orders_users
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

-- =====================================================
-- 5. TABLE: order_details
-- =====================================================

CREATE TABLE IF NOT EXISTS order_details (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,

    CONSTRAINT fk_order_details_orders
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_order_details_products
        FOREIGN KEY (product_id)
        REFERENCES products(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

-- =====================================================
-- 6. INITIAL SEED DATA
-- =====================================================

INSERT INTO categories (id, name, description) VALUES
(1, 'Coffee', 'Aneka olahan kopi espresso, americano, latte, dan racikan kopi spesial.'),
(2, 'Non-Coffee', 'Pilihan minuman non-kopi seperti matcha, chocolate, artisan tea, dan varian segar lainnya.'),
(3, 'Pastry & Bakery', 'Aneka roti, croissant, cake, dan pastry segar pendamping minum kopi.')
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description);

INSERT INTO products (id, category_id, name, description, price, stock, is_available) VALUES
(1, 1, 'Americano', 'Espresso klasik dengan tambahan air panas berkualitas, menghadirkan aroma kopi yang kuat, crema lembut, dan rasa bersih menyegarkan.', 25000.00, 20, 1),
(2, 1, 'Caramel Latte', 'Perpaduan espresso mantap, susu segar hangat yang creamy, dan sirup karamel legit dengan sentuhan rasa manis gurih yang memanjakan lidah.', 30000.00, 26, 1),
(3, 1, 'Aren Latte', 'Kopi susu kekinian favorit dengan racikan espresso pilihan, susu murni lembut, dan manis legit dari gula aren alami yang harum.', 35000.00, 50, 1),
(4, 2, 'Matcha Latte', 'Matcha murni khas Jepang berpadu dengan susu segar lembut dan manis pas, menyajikan rasa teh hijau otentik yang menenangkan.', 28000.00, 20, 1),
(5, 2, 'Signature Chocolate', 'Cokelat pekat dark cocoa pilihan dipadukan dengan kelembutan susu murni, menghadirkan cita rasa cokelat intens dan nikmat disajikan dingin maupun hangat.', 26000.00, 25, 1),
(6, 3, 'Butter Croissant', 'Croissant renyah berlapis dengan aroma butter Prancis yang wangi gurih, tekstur luar renyah (flaky) dan bagian dalam lembut berserat.', 22000.00, 15, 1),
(7, 3, 'Cinnamon Roll', 'Roti gulung lembut beraroma kayu manis harum dengan lelehan cream cheese glaze gurih manis yang lumer di mulut.', 24000.00, 12, 1)
ON DUPLICATE KEY UPDATE category_id=VALUES(category_id), name=VALUES(name), description=VALUES(description), price=VALUES(price), stock=VALUES(stock), is_available=VALUES(is_available);

-- =====================================================
-- END OF DATABASE SCHEMA
-- =====================================================
