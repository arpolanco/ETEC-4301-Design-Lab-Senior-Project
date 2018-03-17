package com.mygdx.game;

import com.badlogic.gdx.Game;

import java.io.FileNotFoundException;

public class Main extends Game {

    @Override
    public void create() {
        try {
            setScreen(new DroneLaserTag());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}