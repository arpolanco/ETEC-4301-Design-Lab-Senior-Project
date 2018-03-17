package com.mygdx.GUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;


public class GUILayout{

    private static final Color COLOR = Color.WHITE;
    Vector2 position;
    public Joystick leftJoystick;
    public Joystick rightJoystick;
    StatusBar HealthBar;
    StatusBar ShootBar;
    int joyStickRad;


    public GUILayout(Viewport viewport) {
        init(viewport);

    }

    public GUILayout(Viewport viewport, Vector2 postion) {
        init(viewport);
    }

    public void init(Viewport viewport) {
        position = new Vector2();
        joyStickRad = 50;
        System.out.println("viewport"+joyStickRad);

        //leftJoystick = new Joystick(viewport, new Vector2((int)(viewport.getWorldWidth()*.33), (int)(viewport.getWorldWidth() *.66)), joyStickRad, Color.RED);
        leftJoystick = new Joystick(new Vector2((int)(viewport.getWorldWidth()*.25), (int)(viewport.getWorldHeight() *.25)), joyStickRad, Color.RED);
        System.out.println(leftJoystick.initPos);
        rightJoystick = new Joystick(new Vector2((int)(viewport.getWorldWidth()*.75), (int)(viewport.getWorldHeight() *.25)), joyStickRad, Color.WHITE);
        //HealthBar = new StatusBar()
    }


    public void update(float delta, Viewport viewport) {

    }


    public void render(ShapeRenderer renderer) {
        //HealthBar.render(renderer);
        //ShootBar.render(renderer);
        leftJoystick.render(renderer);
        rightJoystick.render(renderer);
        //renderer.line(position, new Vector2((position.x+temp.x * 20), (position.y+temp.y * 20)));
    }
}

