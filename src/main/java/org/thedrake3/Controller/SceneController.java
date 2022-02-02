package org.thedrake3.Controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.thedrake3.TheDrakeApplication;

import java.io.IOException;
import java.util.Objects;

public class SceneController {

    public static Scene getMainMenuScene(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TheDrakeApplication.class.getResource( "mainMenu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 300);

        scene.getStylesheets().add(
                Objects.requireNonNull(TheDrakeApplication.class.getResource("mainMenu.css")).toExternalForm());

        VBox vbox = (VBox)scene.getRoot().lookup("#mainMenuVBox");
        vbox.prefHeightProperty().bind(scene.heightProperty().divide(2));
        vbox.prefWidthProperty().bind(scene.widthProperty().divide(2));
        vbox.setLayoutX(50);
        vbox.setLayoutY(30);
        vbox.setMinWidth(300);

        for (Node node : vbox.getChildren()) {
            if (node instanceof Button) {
                ((Button) node).prefWidthProperty().bind(vbox.widthProperty().divide(2));
                ((Button) node).prefHeightProperty().bind(vbox.heightProperty().divide(10));
            }
            if (node instanceof Label) {
                ((Label) node).prefWidthProperty().bind(vbox.widthProperty());
                ((Label) node).prefHeightProperty().bind(vbox.heightProperty().divide(3));
            }
        }
        return scene;
    }

    public static Scene getGameScene(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TheDrakeApplication.class.getResource( "theGame.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 300);
        stage.setMinWidth(300);
        stage.setMinHeight(300);

        scene.getStylesheets().add(
                Objects.requireNonNull(TheDrakeApplication.class.getResource("theGame.css")).toExternalForm());

        GridPane gameMap = (GridPane)scene.getRoot().lookup("#gameMap");
        VBox blueCaptures = (VBox)scene.getRoot().lookup("#blueCaptures");
        VBox orangeCaptures = (VBox)scene.getRoot().lookup("#orangeCaptures");
        HBox gameMainHBox = (HBox)scene.getRoot().lookup("#gameMainHBox");
        HBox blueCapturesHBox = (HBox)scene.getRoot().lookup("#blueCapturesHBox");
        HBox orangeCapturesHBox = (HBox)scene.getRoot().lookup("#orangeCapturesHBox");
        HBox gameMapHBox = (HBox)scene.getRoot().lookup("#gameMapHBox");
        VBox middleVBox = (VBox)scene.getRoot().lookup("#middleVBox");
        HBox blueStack = (HBox)scene.getRoot().lookup("#blueStack");
        HBox orangeStack = (HBox)scene.getRoot().lookup("#orangeStack");
        VBox blueStackVBox = (VBox)scene.getRoot().lookup("#blueStackVBox");
        VBox orangeStackVBox = (VBox)scene.getRoot().lookup("#orangeStackVBox");
        VBox gameMapVBox = (VBox)scene.getRoot().lookup("#gameMapVBox");

        gameMainHBox.setViewOrder(100);
        orangeCapturesHBox.setViewOrder(99);
        blueCapturesHBox.setViewOrder(99);
        gameMapVBox.setViewOrder(98);
        blueStackVBox.setViewOrder(90);
        orangeStackVBox.setViewOrder(90);
        blueStack.setViewOrder(80);
        orangeStack.setViewOrder(80);
        gameMapVBox.setViewOrder(95);
        gameMapHBox.setViewOrder(94);


        gameMainHBox.prefWidthProperty().bind(scene.widthProperty());
        gameMainHBox.prefHeightProperty().bind(scene.heightProperty());

        middleVBox.prefWidthProperty().bind(gameMainHBox.widthProperty().divide(2));

        gameMapVBox.prefHeightProperty().bind(middleVBox.heightProperty().multiply(0.7));
        gameMapHBox.prefHeightProperty().bind(gameMapVBox.widthProperty());

        gameMap.prefWidthProperty().bind(gameMapHBox.heightProperty());


        blueStackVBox.prefHeightProperty().bind(middleVBox.heightProperty().multiply(0.15));
        orangeStackVBox.prefHeightProperty().bind(middleVBox.heightProperty().multiply(0.15));
        blueStack.prefHeightProperty().bind(blueStackVBox.widthProperty().divide(8));
        orangeStack.prefHeightProperty().bind(orangeStackVBox.widthProperty().divide(8));

        blueCapturesHBox.prefWidthProperty().bind(gameMainHBox.widthProperty().divide(5));
        orangeCapturesHBox.prefWidthProperty().bind(gameMainHBox.widthProperty().divide(5));
        blueCaptures.prefWidthProperty().bind(blueCapturesHBox.heightProperty().divide(8));
        orangeCaptures.prefWidthProperty().bind(orangeCapturesHBox.heightProperty().divide(8));

        GameController gameController = fxmlLoader.getController();
        gameController.initGame();

        return scene;
    }
}
