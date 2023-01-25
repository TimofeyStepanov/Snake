package com.example.snake.game.drawers;

import com.example.snake.game.graphic.Cell;

public interface Drawer {
    void clearCell(Cell cell, int cellSize);

    void drawCell(Cell cell, int cellSize);
}
