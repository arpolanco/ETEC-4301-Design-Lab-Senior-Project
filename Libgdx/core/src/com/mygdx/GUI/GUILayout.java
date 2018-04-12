package com.mygdx.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;


public class GUILayout{

    private static final Color COLOR = Color.WHITE;
    Vector2 position;
    public Joystick leftJoystick;
    public Joystick rightJoystick;
    StatusBar healthBar;
    StatusBar shootBar;
    int joyStickRad;
    Texture image;




    public GUILayout(Viewport viewport) {
        init(viewport);

    }

    public GUILayout(Viewport viewport, Vector2 postion) {
        init(viewport);
    }

    public void init(Viewport viewport) {
        position = new Vector2();
        joyStickRad = 50;
        leftJoystick = new Joystick(new Vector2((int)(viewport.getWorldWidth()*.25), (int)(viewport.getWorldHeight() *.25)), joyStickRad, Color.RED);
        rightJoystick = new Joystick(new Vector2((int)(viewport.getWorldWidth()*.75), (int)(viewport.getWorldHeight() *.25)), joyStickRad, Color.WHITE);
        healthBar = new StatusBar(new Vector2(viewport.getWorldWidth()*.01f,(int)(viewport.getWorldHeight()*.95)), (int)(viewport.getWorldWidth()*.4), (int)(viewport.getWorldHeight()*.03), 100, Color.FIREBRICK);
        shootBar = new StatusBar(new Vector2(viewport.getWorldWidth()*.01f,(int)(viewport.getWorldHeight()*.9)), (int)(viewport.getWorldWidth()*.4), (int)(viewport.getWorldHeight()*.03), 100, Color.YELLOW);
    }



    public void update(Vector3 input) {

        leftJoystick.update(new Vector2(input.x,input.y));
        rightJoystick.update(new Vector2(input.x,input.y));

    }




    public void render(ShapeRenderer renderer, SpriteBatch spriteBatch ) {
        //HealthBar.render(renderer);
        //ShootBar.render(renderer);
        spriteBatch.draw(image,0,0,0,0);
        leftJoystick.render(renderer);
        rightJoystick.render(renderer);
        healthBar.render(renderer);
        shootBar.render(renderer);
        //renderer.line(position, new Vector2((position.x+temp.x * 20), (position.y+temp.y * 20)));
    }

    public void imageFeed(Texture img) {
        image = img;
    }
}

