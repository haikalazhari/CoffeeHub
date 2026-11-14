package com.voidsyntax.coffeehub.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static final String CONFIG_FILE = "database.properties";

    private static Properties loadProperties() throws SQLException {
        Properties properties = new Properties();

        Path configPath = Path.of(CONFIG_FILE);

        if (!Files.exists(configPath)) {
            throw new SQLException(
                "File konfigurasi database.properties tidak ditemukan. " +
                "Pastikan file berada di folder utama proyek CoffeeHub."
            );
        }

        try (InputStream input = Files.newInputStream(configPath)) {
            properties.load(input);
        } catch (IOException e) {
            throw new SQLException(
                "Gagal membaca konfigurasi database.",
                e
            );
        }

        return properties;
    }

    public static Connection getConnection() throws SQLException {

        Properties properties = loadProperties();

        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password", "");

        if (url == null || url.isBlank()
                || username == null || username.isBlank()) {
            throw new SQLException(
                "Konfigurasi db.url atau db.username belum diisi."
            );
        }

        return DriverManager.getConnection(
            url.trim(),
            username.trim(),
            password
        );
    }
}
