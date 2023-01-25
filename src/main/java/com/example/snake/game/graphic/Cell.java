package com.example.snake.game.graphic;

public final class Cell implements Cloneable {
    public int x, y;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void copy(Cell cell) {
        this.x = cell.x;
        this.y = cell.y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Cell)) {
            return false;
        }

        Cell objCell = (Cell)obj;
        if (this.x != objCell.x) return false;
        return this.y == objCell.y;
    }

    @Override
    public Cell clone() {
        return new Cell(x, y);
    }
}
