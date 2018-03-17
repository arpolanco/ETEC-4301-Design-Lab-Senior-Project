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
    int baseVal = 100;
    int curVal = 100;
    int width = 100;
    int height = 100;
    Color frontBarColor = Color.RED;
    Viewport viewport;

    public StatusBar()
    {
    }

    public StatusBar(Viewport viewport, Vector2 pos, int width, int height, int baseValue, Color col)
    {
        init(viewport, pos, width, height, baseVal, col);
    }



    public void init(Viewport viewport, Vector2 pos, int width, int height, int baseVal, Color col) {
        this.viewport = viewport;
        position = pos;
        this.width = width;
        this.height = height;
        this.baseVal = baseVal;
        setFrontBarColor(col);
        backgroundBar = new Rectangle(pos.x, pos.y, width, height);
        frontBar = new Rectangle(backgroundBar);
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
        renderer.rect(frontBar.getX(), frontBar.getY(), frontBar.getWidth(), frontBar.getHeight());
        renderer.setColor(frontBarColor);
    }


}
