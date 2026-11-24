package com.voidsyntax.coffeehub;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/login.fxml")
        );

        Parent root = loader.load();
        Scene scene = new Scene(root, 900, 600);

        primaryStage.setTitle("CoffeeHub - Login");

        Image appIcon = new Image(
                getClass().getResourceAsStream("/images/coffeehub-logo.png")
        );

        primaryStage.getIcons().add(appIcon);
        primaryStage.setScene(scene);

        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(550);

        // Membuka aplikasi dalam kondisi maximized
        primaryStage.setMaximized(true);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}