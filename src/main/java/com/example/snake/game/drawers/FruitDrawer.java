package com.example.snake.game.drawers;

import com.example.snake.game.graphic.Cell;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class FruitDrawer implements Drawer {
    public static FruitDrawer fruitDrawer = null;

    private enum FruitType {
        APPLE(new Image("apple.png")), WATERMELON(new Image("watermelon.png")), CHERRY(new Image("cherry.png"));
        final Image image;

        FruitType(Image image) {
            this.image = image;
        }

        public static FruitType fruitType;

        public static FruitType getRandomFruit() {
            FruitType[] fruitColors = FruitType.values();
            int randomColorNumber = (int) (Math.random() * ((fruitColors.length - 1) + 1));
            fruitType = fruitColors[randomColorNumber];
            return fruitType;
        }
    }

    public static Drawer getInstance(GraphicsContext graphicsContext) {
        if (fruitDrawer == null) {
            fruitDrawer = new FruitDrawer(graphicsContext);
        }
        return fruitDrawer;
    }

    private final GraphicsContext graphicsContext;
    private FruitDrawer(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }


    public void redrawFruit(Cell cell, int objectSize) {
        Platform.runLater(() -> {
            graphicsContext.drawImage(FruitType.fruitType.image, cell.x, cell.y, objectSize, objectSize);
        });
    }

    @Override
    public void clearCell(Cell cell, int objectSize) {
        Platform.runLater(() -> {
            graphicsContext.setFill(Color.LAVENDER);
            graphicsContext.fillRect(cell.x, cell.y, objectSize, objectSize);
        });
    }

    @Override
    public void drawCell(Cell cell, int objectSize) {
        Platform.runLater(() -> {
            graphicsContext.drawImage(FruitType.getRandomFruit().image, cell.x, cell.y, objectSize, objectSize);
        });
    }
}
