package com.example.snake.game.logic;

import java.io.*;

public class Settings implements Serializable {
    private static Settings settings;
    private static final String FILE_NAME_TO_GET_AND_SAVE_OBJECT = "save.txt";

    public static Settings getInstance() {
        if (settings != null) {
            return settings;
        }

        try {
            settings = tryToGetObjectFromFile();
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
        if (settings == null) {
            settings = new Settings();
        }
        return settings;
    }
    private static Settings tryToGetObjectFromFile() throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(FILE_NAME_TO_GET_AND_SAVE_OBJECT));
        return (Settings) objectInputStream.readObject();
    }

    public static void saveObject() {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(FILE_NAME_TO_GET_AND_SAVE_OBJECT));
            objectOutputStream.writeObject(settings);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void setRecord(int recode) {
        if (settings != null) settings.record = recode;
    }
    public static void setLevel(GameSnake.Level level) {
        if (settings != null) settings.level = level;
    }
    public static void setMode(GameSnake.Mode mode) {
        if (settings != null) settings.mode = mode;
    }

    public int record;
    public GameSnake.Level level;
    public GameSnake.Mode mode;

    private Settings() {
        record = 0;
        level = GameSnake.Level.MEDIUM;
        mode = GameSnake.Mode.WITH_BORDERS;
    }

}
