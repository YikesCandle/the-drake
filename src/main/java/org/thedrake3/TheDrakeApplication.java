package org.thedrake3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.thedrake3.Controller.SceneController;

import java.io.IOException;
import java.util.Objects;


public class TheDrakeApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = SceneController.getMainMenuScene(stage);


        Image icon = new Image(
                Objects.requireNonNull(TheDrakeApplication.class.getResource("gameLogo.jpg")).toExternalForm());

        stage.getIcons().add(icon);
        stage.setTitle("TheDrake");
        stage.setMinHeight(300);
        stage.setMinWidth(500);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}