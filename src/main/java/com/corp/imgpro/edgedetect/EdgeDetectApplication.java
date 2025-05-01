package com.corp.imgpro.edgedetect;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main application class for the Edge Detection application using JavaFX.
 * This class serves as the entry point for the application and initializes
 * the primary stage with the main view.
 * The application provides a graphical interface for performing edge detection
 * operations on images using the Sobel method.
 */
public class EdgeDetectApplication extends Application {
    
    /**
     * Starts the JavaFX application by loading the main FXML view and displaying
     * it in the primary stage.
     *
     * @param stage The primary stage for this application
     * @throws IOException If the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(EdgeDetectApplication.class.getResource("edgedetect-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1920, 1080);
        stage.setTitle("Edge Detection with Sobel Method");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main entry point for the application.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }
}