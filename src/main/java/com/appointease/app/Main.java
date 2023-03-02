package com.appointease.app;

import com.appointease.app.utils.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * The main class of the AppointEase application.
 */
public class Main extends Application {
    /**
     * The entry point of the AppointEase application.
     *
     * @param stage The entry stage for this application.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/login-screen.fxml"));
        Parent screen = fxmlLoader.load();
        Scene scene = new Scene(screen);
        stage.setScene(scene);
        stage.setTitle("Login Screen");
        stage.show();
    }

    /**
     * The main method of the AppointEase application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) throws IOException {
        // Locale.setDefault(Locale.FRENCH);
        JDBC.openConnection();
        // DBHelpers.setUpDB();
        File file = new File("login_activity.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        launch();
        JDBC.closeConnection();
    }

}