package com.example.snake.game.drawers;

import com.example.snake.game.graphic.Cell;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class SnakeDrawer implements Drawer {
    private static SnakeDrawer snakeDrawer = null;
    public static Drawer getInstance(GraphicsContext graphicsContext) {
        if (snakeDrawer == null) {
            snakeDrawer = new SnakeDrawer(graphicsContext);
        }
        return snakeDrawer;
    }

    private static final String HEAD_IMAGE_FILE_NAME = "head.png";
    private static final String BODY_IMAGE_FILE_NAME = "body.png";

    private static boolean needToDrawFruit = false;
    public static void needToRedrawFruitAfterSnakeMoving() {
        needToDrawFruit = true;
    }

    private final GraphicsContext graphicsContext;
    private SnakeDrawer(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    @Override
    public void clearCell(Cell cell, int objectSize) {
        Platform.runLater(() -> {
            graphicsContext.setFill(Color.LAVENDER);
            graphicsContext.fillRect(cell.x, cell.y, objectSize, objectSize);

            if (needToDrawFruit) {
                FruitDrawer.fruitDrawer.drawCell(cell, objectSize);
                needToDrawFruit = false;
            }
        });
    }

    @Override
    public void drawCell(Cell cell, int objectSize) {
        Platform.runLater(() -> {
            graphicsContext.setFill(Color.GREEN);
            graphicsContext.fillRect(cell.x, cell.y, objectSize, objectSize);
        });
    }

    public void drawSnakeHead(Cell newHead, Cell oldHead, int objectSize) {
        Platform.runLater(() -> {
            graphicsContext.drawImage(new Image(HEAD_IMAGE_FILE_NAME), newHead.x, newHead.y, objectSize, objectSize);
            if (oldHead != null) {
                graphicsContext.drawImage(new Image(BODY_IMAGE_FILE_NAME), oldHead.x, oldHead.y, objectSize, objectSize);
            }
        });
    }
}
