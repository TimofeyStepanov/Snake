package com.example.snake.game.graphic;


import com.example.snake.game.drawers.Drawer;
import com.example.snake.game.logic.GameSnake;

public abstract class GraphicObject {
    public static abstract class Builder {
        protected int objectSize;
        protected Drawer drawer;

        public Builder objectSize(int objectSize) {
            this.objectSize = objectSize;
            return this;
        }
        public Builder drawer(Drawer drawer) {
            this.drawer = drawer;
            return this;
        }

        public abstract GraphicObject build();
    }

    public abstract void clear();
    public abstract void draw(Cell cell);
}
