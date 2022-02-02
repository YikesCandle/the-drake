package org.thedrake3.Textures;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import org.thedrake3.TheDrakeApplication;

import java.net.Inet4Address;
import java.util.Objects;

public class CardTexture {

    private static final double x = 744.0;
    private static final double y = 478.0;
    private static final Integer blueStartX = 73;
    private static final Integer blueStartY = 12;
    private static final Integer orangeStartX = 73;
    private static final Integer orangeStartY = 246;
    private static final double gapX = 3.2;
    private static final double gapY = 3.2;


    public static ImagePattern CardImagePattern(Image image, Integer posX, Integer posY) {
        return new ImagePattern(image, -posX/100.0, -posY/100.0, x/104, y/104, true);
    }

    public static ImagePattern getTroop(Integer id, boolean isOrange, boolean isReverse) {
        int xPos, yPos;
        if (isOrange) {
            xPos = orangeStartX;
            yPos = orangeStartY;
        } else {
            xPos = blueStartX;
            yPos = blueStartY;
        }
        xPos += (gapX + 104) * id;
        if (isReverse)
            yPos += gapY + 104;
        Image texture = new Image(Objects.requireNonNull(
                TheDrakeApplication.class.getResource("texture.png")).toExternalForm());
        return CardImagePattern(texture, xPos, yPos);
    }

    public static Rectangle getTroopRectangle(String name, boolean isOrange, boolean isReverse) {
        int id = -1;
        name = name.toLowerCase();
        if (name.equals("drake"))
            id = 0;
        if (name.equals("clubman"))
            id = 1;
        if (name.equals("monk"))
            id = 2;
        if (name.equals("spearman"))
            id = 3;
        if (name.equals("swordsman"))
            id = 4;
        if (name.equals("archer"))
            id = 5;
        if (id == -1)
            throw new RuntimeException();
        ImagePattern imagePattern = getTroop(id, isOrange, isReverse);
        Rectangle result = new Rectangle(100, 100, imagePattern);
        if (isOrange) result.setRotate(180);
        return result;
    }
    public static Rectangle getMountainRectangle() {
        Image texture = new Image(Objects.requireNonNull(
                TheDrakeApplication.class.getResource("mountain5.png")).toExternalForm());
        ImagePattern imagePattern =
                new ImagePattern(texture, 0, 0, 1, 1, true);
        return new Rectangle(100, 100, imagePattern);
    }
}
