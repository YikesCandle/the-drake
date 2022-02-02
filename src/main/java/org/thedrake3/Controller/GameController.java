package org.thedrake3.Controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.thedrake3.Textures.CardTexture;
import org.thedrake3.core.*;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class GameController {

    @FXML
    VBox blueCaptures, orangeCaptures, middleVBox, blueStackVBox, orangeStackVBox, gameMapVBox;
    @FXML
    HBox gameMainHBox, blueCapturesHBox, orangeCapturesHBox, gameMapHBox, blueStack, orangeStack;
    @FXML
    GridPane gameMap;

    GameState gameState;

    public void initGame() {
        Board board = new Board(4);
        PositionFactory pf = board.positionFactory();
        board = board.withTiles(new Board.TileAt(pf.pos("c2"), BoardTile.MOUNTAIN));
        board = board.withTiles(new Board.TileAt(pf.pos("a3"), BoardTile.MOUNTAIN));
        gameState = new StandardDrakeSetup().startState(board);

        initializeStack(true);
        initializeStack(false);
        addUnitToMap(CardTexture.getMountainRectangle(), gameMap ,3, 2, false);
        addUnitToMap(CardTexture.getMountainRectangle(), gameMap ,1, 3, false);
        updateColors();
    }

    public void addUnitToNode(Rectangle rectangle, Node node) {
        if (node instanceof VBox) {
            rectangle.heightProperty().bind(((VBox) node).heightProperty().divide(8));
            rectangle.widthProperty().bind(((VBox) node).widthProperty());
            ((VBox) node).getChildren().add(rectangle);
            return;
        }
        if (node instanceof HBox) {
            rectangle.widthProperty().bind(((HBox) node).widthProperty().divide(8));
            rectangle.heightProperty().bind(((HBox) node).heightProperty());
            ((HBox) node).getChildren().add(rectangle);
            return;
        }
        throw new RuntimeException();
    }

    public void addUnitToMap(Rectangle rectangle, Node node, int x, int y, boolean dragProperty) {
        rectangle.widthProperty().bind(((GridPane) node).widthProperty().divide(4));
        rectangle.heightProperty().bind(((GridPane) node).heightProperty().divide(4));
        if (dragProperty) setDragMapProperty(rectangle);
        ((GridPane) node).add(rectangle, x - 1, 4 - y);
    }

    public void initializeStack(boolean isOrange) {
        HBox stack = isOrange ? orangeStack : blueStack;
        String [] allUnits = {"drake", "clubman", "clubman", "monk", "spearman", "swordsman", "archer"};
        for (String unit : allUnits) {
            addUnitToNode(CardTexture.getTroopRectangle(unit, isOrange, false), stack);
        }
        setDragStackProperty(stack);
    }

    public void moveUnitFromStackToMap(HBox stack, int x, int y) {
        removeDragStackProperty(stack);
        Rectangle rectangle = (Rectangle)stack.getChildren().get(0);
        stack.getChildren().remove(0);
        addUnitToMap(rectangle, gameMap, x, y, true);
        setDragStackProperty(stack);

    }

    public void setDragStackProperty(HBox stack) {
        if (stack.getChildren().size() > 0) {
            Rectangle rectangle = (Rectangle)stack.getChildren().get(0);
            AtomicReference<Double> x = new AtomicReference<>((double) 0);
            AtomicReference<Double> y = new AtomicReference<>((double) 0);
            rectangle.setOnMousePressed(event -> {
                x.set(event.getSceneX() - rectangle.getTranslateX());
                y.set(event.getSceneY() - rectangle.getTranslateY());

                DropShadow shadow1 = new DropShadow();
                shadow1.setColor(Color.BLACK);
                shadow1.setSpread(0.2);
                shadow1.setBlurType(BlurType.THREE_PASS_BOX);
                shadow1.setRadius(100);
                rectangle.setEffect(shadow1);
                rectangle.setViewOrder(-1);

                event.consume();

            });
            rectangle.setOnMouseDragged(event -> {
                rectangle.setTranslateX(event.getSceneX() - x.get());
                rectangle.setTranslateY(event.getSceneY() - y.get());

                event.consume();
            });
            rectangle.setOnMouseReleased(event -> {
                rectangle.setEffect(null);

                Bounds rectBounds = rectangle.localToScene(rectangle.getBoundsInLocal());
                Bounds mapBounds = gameMap.localToScene(gameMap.getBoundsInLocal());
                if (mapBounds.contains(rectBounds.getCenterX(), rectBounds.getCenterY())
                        && ((gameState.sideOnTurn() == PlayingSide.ORANGE && stack.getId().equals("orangeStack"))
                            ||  (gameState.sideOnTurn() == PlayingSide.BLUE && stack.getId().equals("blueStack")))) {
                    Pair<Integer, Integer> mapPoint = getMapCoords(rectangle);
                    int mapX = mapPoint.getKey(), mapY = mapPoint.getValue();
                    rectangle.setTranslateX(0);
                    rectangle.setTranslateY(0);
                    if (gameState.canPlaceFromStack(new BoardPos(4, mapX - 1, mapY - 1))) {
                        gameState = gameState.placeFromStack(new BoardPos(4, mapX - 1, mapY - 1));
                        moveUnitFromStackToMap(stack, mapX, mapY);
                    }
                    updateColors();
                    if (!isPlayerOnTurnHavingValidMoves())
                    {
                        event.consume();
                        endOfGame();
                        return;
                    }

                } else {
                    rectangle.setTranslateX(0);
                    rectangle.setTranslateY(0);
                }
                rectangle.setViewOrder(0);
                event.consume();
            });
            rectangle.cursorProperty().set(Cursor.HAND);
        }
    }

    public void removeDragStackProperty(HBox stack) {
        if (stack.getChildren().size() > 0) {
            Rectangle rectangle = (Rectangle)stack.getChildren().get(0);
            rectangle.setOnMousePressed(null);
            rectangle.setOnMouseDragged(null);
            rectangle.setOnMouseReleased(null);
            rectangle.cursorProperty().set(null);
        }
    }

    public void setDragMapProperty(Rectangle rectangle) {
        AtomicReference<Double> x = new AtomicReference<>((double) 0);
        AtomicReference<Double> y = new AtomicReference<>((double) 0);

        AtomicReference<Integer> originMapX = new AtomicReference<>(0);
        AtomicReference<Integer> originMapY = new AtomicReference<>(0);

        rectangle.setOnMousePressed(event -> {
            x.set(event.getSceneX() - rectangle.getTranslateX());
            y.set(event.getSceneY() - rectangle.getTranslateY());
            Pair<Integer, Integer> mapPoint = getMapCoords(rectangle);
            originMapX.set(mapPoint.getKey());
            originMapY.set(mapPoint.getValue());

            DropShadow shadow1 = new DropShadow();
            shadow1.setColor(Color.BLACK);
            shadow1.setSpread(0.2);
            shadow1.setBlurType(BlurType.THREE_PASS_BOX);
            shadow1.setRadius(100);
            rectangle.setEffect(shadow1);
            rectangle.setViewOrder(-1);

            event.consume();

        });
        rectangle.setOnMouseDragged(event -> {
            rectangle.setTranslateX(event.getSceneX() - x.get());
            rectangle.setTranslateY(event.getSceneY() - y.get());
            event.consume();
        });
        rectangle.setOnMouseReleased(event -> {
            rectangle.setEffect(null);

            Bounds rectBounds = rectangle.localToScene(rectangle.getBoundsInLocal());
            Bounds mapBounds = gameMap.localToScene(gameMap.getBoundsInLocal());
            if (mapBounds.contains(rectBounds.getCenterX(), rectBounds.getCenterY())) {
                Pair<Integer, Integer> newCoords = getMapCoords(rectangle);
                int mapX = newCoords.getKey(), mapY = newCoords.getValue();
                BoardPos newPos = new BoardPos(4, mapX - 1, mapY - 1);
                BoardPos originPos = new BoardPos(4, originMapX.get() - 1, originMapY.get() - 1);
                Optional<TroopTile> troopTile = gameState.armyOnTurn().boardTroops().at(originPos);
                rectangle.setTranslateX(0);
                rectangle.setTranslateY(0);
                if (troopTile.isPresent()) {
                    for (Move move : troopTile.get().movesFrom(originPos, gameState)) {
                        if (move.target().equals(newPos)) {
                            if (gameState.armyNotOnTurn().boardTroops().at(move.target()).isPresent()) {
                                VBox captures = blueCaptures;
                                if (gameState.sideOnTurn() == PlayingSide.ORANGE)
                                    captures = orangeCaptures;
                                Rectangle newRectangle = (Rectangle)getNodeByRowColumnIndex(4 - mapY, mapX - 1, gameMap);
                                removeDragMapProperty(newRectangle);
                                addUnitToNode(newRectangle, captures);
                            }
                            Rectangle newRectangle = CardTexture.getTroopRectangle(
                                    troopTile.get().troop().name(),
                                    troopTile.get().side() == PlayingSide.ORANGE,
                                    troopTile.get().face() != TroopFace.REVERS
                            );
                            gameMap.getChildren().remove(rectangle);
                            gameState = move.execute(gameState);
                            if (gameState.armyNotOnTurn().boardTroops().at(originPos).isEmpty()) {
                                addUnitToMap(newRectangle, gameMap, mapX, mapY, true);
                            }
                            else {
                                addUnitToMap(newRectangle, gameMap, originMapX.get(), originMapY.get(), true);
                            }
                            break;
                        }
                    }
                }
                updateColors();
                if (gameState.result() == GameResult.VICTORY || !isPlayerOnTurnHavingValidMoves())
                {
                    event.consume();
                    endOfGame();
                    return;
                }
                rectangle.setTranslateX(0);
                rectangle.setTranslateY(0);

            }

            rectangle.setTranslateX(0);
            rectangle.setTranslateY(0);
            rectangle.setViewOrder(0);
            event.consume();
        });
        rectangle.cursorProperty().set(Cursor.HAND);
    }

    public void removeDragMapProperty(Rectangle rectangle) {
        rectangle.setOnMousePressed(null);
        rectangle.setOnMouseDragged(null);
        rectangle.setOnMouseReleased(null);
        rectangle.cursorProperty().set(null);
    }

    public Pair<Integer, Integer> getMapCoords(Rectangle rectangle) {
        Bounds rectBounds = rectangle.localToScene(rectangle.getBoundsInLocal());
        Bounds mapBounds = gameMap.localToScene(gameMap.getBoundsInLocal());
        int mapX, mapY;
        double dx = mapBounds.getMaxX() - rectBounds.getCenterX();
        double dy = mapBounds.getMaxY() - rectBounds.getCenterY();
        mapX = ((int) (dx / (mapBounds.getWidth() / 4.0))) + 1;
        mapY = ((int) (dy / (mapBounds.getHeight() / 4.0))) + 1;
        mapX = 5 - mapX;
        return new Pair<>(mapX, mapY);
    }

    public Node getNodeByRowColumnIndex (final int row, final int column, GridPane gridPane) {
        Node result = null;
        ObservableList<Node> childrens = gridPane.getChildren();

        for (Node node : childrens) {
            if(node instanceof Rectangle && GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                result = node;
                break;
            }
        }
        return result;
    }

    protected void returnToMainMenu() throws IOException {
        Stage stage = (Stage)(gameMap).getScene().getWindow();
        stage.setScene(SceneController.getMainMenuScene(stage));
        stage.show();
        stage.setFullScreen(false);
        stage.centerOnScreen();
    }

    public void resetGame() {
        gameMap.getChildren().removeIf(node -> node instanceof Rectangle);
        orangeStack.getChildren().removeIf(node -> node instanceof Rectangle);
        blueStack.getChildren().removeIf(node -> node instanceof Rectangle);
        orangeCaptures.getChildren().removeIf(node -> node instanceof Rectangle);
        blueCaptures.getChildren().removeIf(node -> node instanceof Rectangle);
        initGame();
    }

    public void updateColors() {
        if (gameState.sideOnTurn() == PlayingSide.ORANGE) {
            gameMainHBox.setStyle("-fx-background-color: #ffba88");
        }
        if (gameState.sideOnTurn() == PlayingSide.BLUE) {
            gameMainHBox.setStyle("-fx-background-color: #cfefff");
        }
    }

    public boolean isPlayerOnTurnHavingValidMoves() {
        for (int i = 0; i < gameState.board().dimension(); ++i)
            for (int j = 0; j < gameState.board().dimension(); ++j)
                if (gameState.canPlaceFromStack(new BoardPos(gameState.board().dimension(), i, j)))
                    if (!gameState.armyOnTurn().stack().isEmpty())
                        return true;
        for (BoardPos position : gameState.armyOnTurn().boardTroops().troopPositions()) {
            if (gameState.armyOnTurn().boardTroops().at(position).isPresent()) {
                if (!gameState.armyOnTurn().boardTroops().at(position).get().movesFrom(position, gameState).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void endOfGame() {
        String winner = gameState.sideOnTurn() == PlayingSide.BLUE ? "Orange" : "Blue";
        Alert newGame = new Alert(Alert.AlertType.NONE);
        newGame.initOwner(gameMap.getScene().getWindow());
        newGame.setTitle("End of game!");
        newGame.setHeaderText("The winner is " + winner + "!");
        newGame.setContentText("Play again?");
        newGame.getButtonTypes().add(ButtonType.YES);
        newGame.getButtonTypes().add(ButtonType.NO);
        newGame.getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = newGame.showAndWait();
        if (result.get() == ButtonType.NO) {
            try {
                returnToMainMenu();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (result.get() == ButtonType.YES) {
            resetGame();
            return;
        }
        return;
    }
}
