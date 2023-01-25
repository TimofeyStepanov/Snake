package com.example.snake;

import com.example.snake.game.drawers.FruitDrawer;
import com.example.snake.game.drawers.SnakeDrawer;
import com.example.snake.game.logic.GameSnake;
import com.example.snake.game.graphic.Fruit;
import com.example.snake.game.graphic.Snake;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GameSnakeController {
    @FXML
    private Canvas canvas;

    @FXML
    private TextArea largeTextArea;

    @FXML
    private TextField smallTextField;

    @FXML
    private ChoiceBox<String> levelChoiceBox;

    @FXML
    private CheckBox modeCheckBox;

    protected final int CELL_SIZE = 23;

    private GameSnake game;

    @FXML
    public void initialize() {
        System.out.println("I initialize " + Thread.currentThread().getName());

        if (canvas.getWidth() % CELL_SIZE != 0 || canvas.getHeight() % CELL_SIZE != 0) {
            throw new IllegalArgumentException("Wrong size of canvas!");
        }

        levelChoiceBox.getItems().add("EASY");
        levelChoiceBox.getItems().add("MEDIUM");
        levelChoiceBox.getItems().add("HARD");

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setFill(Color.LAVENDER);
        graphicsContext.fillRect(0, 0, canvas.getHeight(), canvas.getWidth());

        Snake snake = (Snake)new Snake.Builder()
                .drawer(SnakeDrawer.getInstance(graphicsContext))
                .objectSize(CELL_SIZE)
                .build();

        Fruit fruit = (Fruit)new Fruit.Builder()
                .drawer(FruitDrawer.getInstance(graphicsContext))
                .objectSize(CELL_SIZE)
                .build();

        game = new GameSnake.Builder()
                .maxCellX((int)canvas.getWidth())
                .maxCellY((int)canvas.getHeight())
                .cellSize(CELL_SIZE)
                .snake(snake)
                .fruit(fruit)
                .mainTextArea(largeTextArea)
                .scoreTextField(smallTextField)
                .levelChooseBox(new GameSnake.VisualObjectFromApplication<>() {
                    @Override
                    public void set(GameSnake.Level level) {
                        levelChoiceBox.setValue(level.toString());
                    }

                    @Override
                    public GameSnake.Level get() {
                        return GameSnake.Level.valueOf(levelChoiceBox.getValue());
                    }

                    @Override
                    public void lock() {
                        levelChoiceBox.setMouseTransparent(true);
                    }

                    @Override
                    public void unlock() {
                        levelChoiceBox.setMouseTransparent(false);
                    }
                })
                .modeCheckBox(new GameSnake.VisualObjectFromApplication<>() {
                    @Override
                    public void set(GameSnake.Mode value) {
                        modeCheckBox.setSelected(value.equals(GameSnake.Mode.WITH_BORDERS));
                    }

                    @Override
                    public GameSnake.Mode get() {
                        return modeCheckBox.isSelected()
                                ? GameSnake.Mode.WITH_BORDERS
                                : GameSnake.Mode.WITHOUT_BORDERS;
                    }

                    @Override
                    public void lock() {
                        modeCheckBox.setMouseTransparent(true);
                    }

                    @Override
                    public void unlock() {
                        modeCheckBox.setMouseTransparent(false);
                    }
                })
                .build();

        largeTextArea.setWrapText(true);
        game.initInfoTextField();
        game.infoAboutStartGame();

        canvas.setFocusTraversable(true);
        this.canvas.setOnKeyPressed(keyEvent -> game.processKey(keyEvent.getText()));

        levelChoiceBox.setFocusTraversable(false);
        modeCheckBox.setFocusTraversable(false);
    }

    public void setClosingSettings(Stage stage){
        stage.setOnCloseRequest((event) -> {
            game.close();
            System.out.println("Close game");
        });
    }
}