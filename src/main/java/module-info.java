module com.example.brickpuzzle {
    requires javafx.controls;
    requires javafx.fxml;
    requires io.reactivex.rxjava2;
    requires awaitility;


    opens com.example.snake to javafx.fxml;
    exports com.example.snake;
    exports com.example.snake.game.graphic;
    opens com.example.snake.game.graphic to javafx.fxml;
    exports com.example.snake.game.logic;
    opens com.example.snake.game.logic to javafx.fxml;
}