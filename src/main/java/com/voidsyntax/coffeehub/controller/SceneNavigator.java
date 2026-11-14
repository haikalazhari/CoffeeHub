package com.voidsyntax.coffeehub.controller;

import java.io.IOException;

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

            stage.setScene(new Scene(root));
            stage.centerOnScreen();

        } catch (IOException | NullPointerException e) {
            System.err.println(
                    "Gagal membuka halaman: " + fxmlPath
            );
            e.printStackTrace();
        }
    }
}
