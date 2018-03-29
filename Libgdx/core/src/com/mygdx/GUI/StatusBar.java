package com.mygdx.GUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by Dude XPS on 2/12/2018.
 */

public class StatusBar {

    Vector2 position;
    Rectangle backgroundBar;
    Rectangle frontBar;
    float baseVal = 100;
    float curVal = 100;
    int width = 100;
    int height = 100;
    Color frontBarColor = Color.RED;
    Viewport viewport;

    public StatusBar()
    {
    }

    public StatusBar(Vector2 pos, int width, int height, float baseValue, Color col)
    {
        init(pos, width, height, baseValue, col);
    }



    public void init(Vector2 pos, int width, int height, float baseVal, Color col) {
        this.viewport = viewport;
        position = pos;
        this.width = width;
        this.height = height;
        this.baseVal = baseVal;
        setFrontBarColor(col);
        backgroundBar = new Rectangle(pos.x, pos.y, width, height);
        frontBar = new Rectangle(pos.x, pos.y, width, height);
        addVal(-20);
    }

    public void update(float delta) {

    }

    public void addVal(int val)
    {
        curVal += val;
        if(curVal < 0)
            curVal = 0;
        else if(curVal > baseVal)
            curVal = baseVal;
    }
    public void setFrontBarColor(Color col)
    {
        frontBarColor = col;
    }

    public void render(ShapeRenderer renderer) {
        renderer.set(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.LIGHT_GRAY);
        renderer.rect(backgroundBar.getX(),backgroundBar.getY(), backgroundBar.getWidth(),backgroundBar.getHeight());
        renderer.setColor(frontBarColor);
        renderer.rect(frontBar.getX(), frontBar.getY(), frontBar.getWidth()*(curVal/baseVal), frontBar.getHeight());
    }


}
