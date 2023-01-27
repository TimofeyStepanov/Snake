package com.example.snake.game.logic;

import com.example.snake.game.drawers.SnakeDrawer;
import com.example.snake.game.exceptions.SnakeCrushedIntoWallException;
import com.example.snake.game.graphic.Cell;
import com.example.snake.game.graphic.Fruit;
import com.example.snake.game.graphic.Snake;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.Closeable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class GameSnake implements Closeable {
    public enum Level {
        HARD(100), MEDIUM(200), EASY(300);
        final int speedOfChangingCell;

        Level(int speedOfChangingCell) {
            this.speedOfChangingCell = speedOfChangingCell;
        }
    }

    public enum Mode {
        WITH_BORDERS(Map.of(
                Snake.Direction.LEFT, (cell) -> {
                    cell.x -= cellSize;
                    if (cell.x < 0) {
                        throw new SnakeCrushedIntoWallException();
                    }
                },
                Snake.Direction.RIGHT, (cell) -> {
                    cell.x += cellSize;
                    if (cell.x >= maxCellX) {
                        throw new SnakeCrushedIntoWallException();
                    }
                },
                Snake.Direction.DOWN, (cell) -> {
                    cell.y += cellSize;
                    if (cell.y >= maxCellY) {
                        throw new SnakeCrushedIntoWallException();
                    }
                },
                Snake.Direction.UP, (cell) -> {
                    cell.y -= cellSize;
                    if (cell.y < 0) {
                        throw new SnakeCrushedIntoWallException();
                    }
                })
        ),
        WITHOUT_BORDERS(Map.of(
                Snake.Direction.LEFT, (cell) -> {
                    cell.x -= cellSize;
                    if (cell.x < 0) {
                        cell.x = maxCellX - cellSize;
                    }
                },
                Snake.Direction.RIGHT, (cell) -> {
                    cell.x += cellSize;
                    if (cell.x >= maxCellX) {
                        cell.x = 0;
                    }
                },
                Snake.Direction.DOWN, (cell) -> {
                    cell.y += cellSize;
                    if (cell.y >= maxCellY) {
                        cell.y = 0;
                    }
                },
                Snake.Direction.UP, (cell) -> {
                    cell.y -= cellSize;
                    if (cell.y < 0) {
                        cell.y = maxCellY - cellSize;
                    }
                })
        );

        final Map<Snake.Direction, Snake.SnakeMover> directionAndItsSnakeMover;

        Mode(Map<Snake.Direction, Snake.SnakeMover> directionAndItsSnakeMover) {
            this.directionAndItsSnakeMover = directionAndItsSnakeMover;
        }
    }

    public interface VisualObjectFromApplication<T> {
        void set(T value);
        T get();
        void lock();
        void unlock();
    }

    private static final Map<Snake.Direction, Snake.Direction> directionAndWrongDirection = Map.of(
            Snake.Direction.DOWN, Snake.Direction.UP,
            Snake.Direction.UP, Snake.Direction.DOWN,
            Snake.Direction.LEFT, Snake.Direction.RIGHT,
            Snake.Direction.RIGHT, Snake.Direction.LEFT
    );

    private final Map<String, Snake.Direction> keyNameAndDirectionMap = Map.of(
            "w", Snake.Direction.UP,
            "s", Snake.Direction.DOWN,
            "a", Snake.Direction.LEFT,
            "d", Snake.Direction.RIGHT
    );

    private static int cellSize, maxCellX, maxCellY;

    private final Snake snake;
    private final Fruit fruit;

    private final TextField scoreTextField;
    private int score = 0;

    private final TextArea mainTextArea;
    private int record;

    private final ScheduledExecutorService executorService;
    private ScheduledFuture<?> scheduledFuture;

    private Level level;
    private final VisualObjectFromApplication<Level> levelChooseBox;

    private Mode mode;
    private final VisualObjectFromApplication<Mode> modeCheckBox;

    private boolean gameIsNotStarted = true;

    public static final class Builder {
        private Snake snake;
        private Fruit fruit;
        private TextField scoreTextField;
        private TextArea mainTextArea;
        private VisualObjectFromApplication<Level> levelChooseBox;
        private VisualObjectFromApplication<Mode> modeCheckBox;
        private int cellSize, maxCellX, maxCellY;

        public Builder snake(Snake snake) {
            this.snake = snake;
            return this;
        }
        public Builder fruit(Fruit fruit) {
            this.fruit = fruit;
            return this;
        }
        public Builder scoreTextField(TextField scoreTextField) {
            this.scoreTextField = scoreTextField;
            return this;
        }
        public Builder mainTextArea(TextArea mainTextArea) {
            this.mainTextArea = mainTextArea;
            return this;
        }
        public Builder levelChooseBox(VisualObjectFromApplication<Level> levelChooseBar) {
            this.levelChooseBox = levelChooseBar;
            return this;
        }
        public Builder modeCheckBox(VisualObjectFromApplication<Mode> modeChooseBar) {
            this.modeCheckBox = modeChooseBar;
            return this;
        }
        public Builder cellSize(int cellSize) {
            this.cellSize = cellSize;
            return this;
        }
        public Builder maxCellX(int maxCellX) {
            this.maxCellX = maxCellX;
            return this;
        }
        public Builder maxCellY(int maxCellY) {
            this.maxCellY = maxCellY;
            return this;
        }
        public GameSnake build() {
            return new GameSnake(this);
        }
    }

    private GameSnake(Builder builder) {
        executorService = Executors.newScheduledThreadPool(1);

        this.snake = builder.snake;
        this.fruit = builder.fruit;
        this.scoreTextField = builder.scoreTextField;
        this.mainTextArea = builder.mainTextArea;
        this.levelChooseBox = builder.levelChooseBox;
        this.modeCheckBox = builder.modeCheckBox;
        maxCellX = builder.maxCellX;
        maxCellY = builder.maxCellY;
        cellSize = builder.cellSize;

        Settings savedSettings = Settings.getInstance();
        this.record = savedSettings.record;
        this.level = savedSettings.level;
        this.mode = savedSettings.mode;

        snake.draw(generateRandomCell());

        levelChooseBox.set(this.level);
        modeCheckBox.set(this.mode);
    }

    public void processKey(String keyTitle) {
        if (keyIsDirection(keyTitle)) {
            if (gameIsNotStarted) {
                levelChooseBox.lock();
                modeCheckBox.lock();

                level = levelChooseBox.get();
                mode = modeCheckBox.get();

                setScore(score);
                initInfoTextField();

                snake.setDirectionCellMoverMap(mode.directionAndItsSnakeMover);
                fruit.draw(generateRandomCell());

                scheduledFuture = executorService.scheduleAtFixedRate(
                        this::step,
                        0,
                        level.speedOfChangingCell,
                        TimeUnit.MILLISECONDS
                );
                gameIsNotStarted = false;
            }

            Snake.Direction newDirection = getDirection(keyTitle);
            Snake.Direction oldDirection = snake.getDirection();

            if (oldDirection == null || !newDirection.equals(directionAndWrongDirection.get(oldDirection))) {
                snake.setDirection(getDirection(keyTitle));
            }
        }
    }

    private void step() {
        if (snakeTailAndFruitAreTheSameCell()) {
            // костыль для перекраски фрукта, если хвост змеи затрет фрукт
            SnakeDrawer.needToRedrawFruitAfterSnakeMoving();
        }

        try {
            snake.move();
        } catch (SnakeCrushedIntoWallException exception) {
            restart();
        }

        if (snakeHeadAndPartOfSnakeBodyAreTheSameCell()) {
            System.out.println("SnakeCrashedToItself");
            restart();
        } else if (snakeHeadAndFruitAreTheSameCell()) {
            score++;
            setScore(score);
            if (score > record) {
                record = score;
                initInfoTextField();
            }

            fruit.draw(generateRandomCell());
            snake.makeBigger();
        }
    }

    private void setScore(int score) {
        Platform.runLater(() -> scoreTextField.setText("Score:" + score));

    }

    private void restart() {
        System.out.println("Snake crashed");

        scheduledFuture.cancel(false);
        gameIsNotStarted = true;

        snake.clear();
        fruit.clear();

        levelChooseBox.unlock();
        modeCheckBox.unlock();

        score = 0;

        infoAboutStartGame();
        snake.draw(generateRandomCell());
    }

    private boolean snakeHeadAndPartOfSnakeBodyAreTheSameCell() {
        Cell snakeHead = snake.getHead();
        List<Cell> snakeBody = snake.getBody();
        return snakeBody.stream().skip(1).anyMatch(bodyCell -> bodyCell.equals(snakeHead));
    }

    private boolean snakeHeadAndFruitAreTheSameCell() {
        Cell snakeHeadCell = snake.getHead();
        Cell fruitCell = fruit.getCell();
        return snakeHeadCell.equals(fruitCell);
    }

    private boolean snakeTailAndFruitAreTheSameCell() {
        Cell snakeTail = snake.getTail();
        Cell fruitCell = fruit.getCell();
        return fruitCell.equals(snakeTail);
    }

    public boolean keyIsDirection(String keyName) {
        return keyNameAndDirectionMap.containsKey(keyName);
    }

    public Snake.Direction getDirection(String keyName) {
        return keyNameAndDirectionMap.get(keyName);
    }

    public void infoAboutStartGame() {
        Platform.runLater(() -> scoreTextField.setText("Move snake"));
    }

    public void initInfoTextField() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Record:");
        stringBuilder.append(record);
        stringBuilder.append('\n');

        for (Map.Entry<String, Snake.Direction> entry : keyNameAndDirectionMap.entrySet()) {
            stringBuilder.append(entry.getValue());
            stringBuilder.append(" --> ");
            stringBuilder.append(entry.getKey());
            stringBuilder.append('\n');
        }

        Platform.runLater(() -> mainTextArea.setText(stringBuilder.toString()));
    }

    private Cell generateRandomCell() {
        int newX = generateRandomDigitInNeededRage(0, maxCellX - cellSize);
        newX -= newX % cellSize;

        int newY = generateRandomDigitInNeededRage(0, maxCellY - cellSize);
        newY -= newY % cellSize;

        return new Cell(newX, newY);
    }

    private int generateRandomDigitInNeededRage(int min, int max) {
        return (int)(Math.random()*((max-min)+1))+min;
    }

    @Override
    public void close() {
        Settings.setRecord(record);
        Settings.setLevel(level);
        Settings.setMode(mode);
        Settings.saveObject();

        executorService.shutdown();
    }
}
