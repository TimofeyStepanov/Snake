package com.example.snake.game.graphic;

import com.example.snake.game.drawers.SnakeDrawer;
import com.example.snake.game.exceptions.SnakeCrushedExceptionIntoWall;

import java.util.*;

public final class Snake extends GraphicObject {
    @FunctionalInterface
    public interface SnakeMover {
        void move(Cell head) throws SnakeCrushedExceptionIntoWall;
    }

    public enum Direction {
        LEFT, RIGHT, UP, DOWN
    }

    private Map<Direction, SnakeMover> directionCellMoverMap;

    private final List<Cell> snakeBody;
    private final int objectSize;
    private int numberOfCellsToAdd = 0;
    private volatile Direction direction;
    private final SnakeDrawer snakeDrawer;

    public static class Builder extends GraphicObject.Builder {
        @Override
        public GraphicObject build() {
            return new Snake(this);
        }
    }

    private Snake(Builder builder) {
        this.objectSize = builder.objectSize;
        this.snakeDrawer = (SnakeDrawer) builder.drawer;

        snakeBody = new ArrayList<>();
    }

    public void move() throws SnakeCrushedExceptionIntoWall {
        Cell tail;
        int numberOfCellToMove = snakeBody.size() - 1;

        if (numberOfCellsToAdd == 0) {
            tail = snakeBody.get(snakeBody.size()-1);
            snakeDrawer.clearCell(tail.clone(), objectSize);
        } else {
            tail = snakeBody.get(snakeBody.size()-1).clone();
            snakeBody.add(tail);

            numberOfCellsToAdd--;
        }

        for (int i = numberOfCellToMove; i > 0; i--) {
            Cell nextCell = snakeBody.get(i-1);
            Cell currentCell = snakeBody.get(i);

            currentCell.copy(nextCell);
        }

        Cell head = snakeBody.get(0);
        directionCellMoverMap.get(direction).move(head);

        Cell cellAfterHead = snakeBody.size() > 1 ? snakeBody.get(1) : null;
        snakeDrawer.drawSnakeHead(head, cellAfterHead, objectSize);
    }

    public void makeBigger() {
        numberOfCellsToAdd++;
    }

    @Override
    public void clear() {
        snakeBody.forEach(bodyCell -> snakeDrawer.clearCell(bodyCell, objectSize));
        snakeBody.clear();
    }
    @Override
    public void draw(Cell cell) {
        snakeBody.add(cell);
        //snakeDrawer.drawCell(cell, objectSize);
        snakeDrawer.drawSnakeHead(cell, null, objectSize);
    }


    public void setDirection(Direction direction) {
        this.direction = direction;
    }
    public Direction getDirection() {
        return this.direction;
    }

    public Cell getHead() {
        return snakeBody.get(0);
    }
    public List<Cell> getBody() {
        return snakeBody;
    }
    public Cell getTail() {
        return snakeBody.get(snakeBody.size()-1);
    }

    public void setDirectionCellMoverMap(Map<Direction, SnakeMover> directionCellMoverMap) {
        this.directionCellMoverMap = directionCellMoverMap;
    }
}
