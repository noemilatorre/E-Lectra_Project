package com.electra.canbusdemo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
/**
 * The MainApplication class is the entry point for the PowerTrain Monitoring and Control GUI application.
 * It extends the JavaFX Application class and initializes the main GUI window.
 *
 */
public class MainApplication extends Application {
    /**
     * The main stage for the application.
     */
    public static Stage stage = null;
    /**
     * The start method is called when the JavaFX application is launched.
     *
     * @param stage The primary stage for the application.
     * @throws IOException If an error occurs during loading the FXML file.
     */
    @Override
    public void start(Stage stage) throws IOException {
        //Imposta un gestore per la richiesta di chiusura per garantire la corretta terminazione dell'applicazione
        stage.setOnCloseRequest(event -> {
           System.exit(0);
        });
        // Set the main stage to the provided stage
        MainApplication.stage = stage;
        // Load the main FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        // Configure the main stage
        stage.setTitle("CANbus DEMO");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}