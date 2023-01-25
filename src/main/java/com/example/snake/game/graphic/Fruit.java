package com.example.snake.game.graphic;

import com.example.snake.game.drawers.FruitDrawer;

public final class Fruit extends GraphicObject {
    private Cell fruitCell;
    private final int objectSize;
    private final FruitDrawer drawer;

    @Override
    public void clear() {
        drawer.clearCell(fruitCell, objectSize);
    }

    @Override
    public void draw(Cell cell) {
        fruitCell = cell;
        drawer.drawCell(cell, objectSize);
    }


    public static class Builder extends GraphicObject.Builder {
        @Override
        public GraphicObject build() {
            return new Fruit(this);
        }
    }

    private Fruit(GraphicObject.Builder builder) {
        this.objectSize = builder.objectSize;

        this.drawer = (FruitDrawer) builder.drawer;
    }

    public Cell getCell() {
        return fruitCell;
    }
}
