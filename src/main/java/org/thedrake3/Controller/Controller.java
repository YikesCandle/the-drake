package org.thedrake3.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;

import java.io.IOException;

public class Controller {

    @FXML
    Button leaveButton;
    @FXML
    Button playSinglePlayerButton;
    @FXML
    Button playMultiPlayerButton;

    @FXML
    protected void exit(ActionEvent event) {
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        Platform.exit();
    }
    @FXML
    protected void playSinglePlayer() {
        System.out.println("Unsupported operation.");
    }
    @FXML
    protected void playMultiplayer(ActionEvent event) throws IOException {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(SceneController.getGameScene(stage));
        stage.show();
        stage.setFullScreen(true);
        event.consume();
    }

}