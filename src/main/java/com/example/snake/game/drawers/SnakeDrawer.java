package com.example.snake.game.drawers;

import com.example.snake.game.graphic.Cell;
import com.example.snake.game.graphic.Snake;
import javafx.application.Platform;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private static final ImageView imageView = new ImageView();
    private static final SnapshotParameters params = new SnapshotParameters();
    private static final Image headImage = new Image(HEAD_IMAGE_FILE_NAME);
    private static final Image bodyImage = new Image(BODY_IMAGE_FILE_NAME);
    {
        imageView.setImage(headImage);
        params.setFill(Color.TRANSPARENT);
    }

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
                FruitDrawer.fruitDrawer.redrawFruit(cell, objectSize);
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

    public void drawSnakeHead(Cell newHead, Snake.Direction direction, Cell oldHead, int objectSize) {
        Platform.runLater(() -> {
            imageView.setRotate(direction.rotateAngle);
            Image rotatedHeadImage = imageView.snapshot(params, null);
            graphicsContext.drawImage(rotatedHeadImage, newHead.x, newHead.y, objectSize, objectSize);
            if (oldHead != null) {
                graphicsContext.drawImage(bodyImage, oldHead.x, oldHead.y, objectSize, objectSize);
            }
        });
    }
}
