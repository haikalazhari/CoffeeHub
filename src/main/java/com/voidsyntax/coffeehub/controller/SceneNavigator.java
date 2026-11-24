package com.voidsyntax.coffeehub.controller;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneNavigator {

    public static void pindahHalaman(
            ActionEvent event,
            String fxmlPath
    ) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneNavigator.class.getResource(fxmlPath)
            );

            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node)
                    event.getSource()).getScene().getWindow();

            // Simpan ukuran Scene sebelumnya
            Scene oldScene = stage.getScene();
            double oldWidth = oldScene.getWidth();
            double oldHeight = oldScene.getHeight();

            // Simpan status window
            boolean maximized = stage.isMaximized();

            // Buat Scene baru menggunakan ukuran sebelumnya
            Scene newScene = new Scene(root, oldWidth, oldHeight);

            stage.setScene(newScene);

            // Pulihkan status window setelah pergantian Scene
            Platform.runLater(() -> {
                if (maximized) {
                    stage.setMaximized(true);
                }
            });

        } catch (IOException | NullPointerException e) {
            System.err.println(
                    "Gagal membuka halaman: " + fxmlPath
            );
            e.printStackTrace();
        }
    }
}