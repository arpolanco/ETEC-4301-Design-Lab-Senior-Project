package com.mygdx.GUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.Viewport;



/**
 * Created by Dude XPS on 2/12/2018.
 */

public class Joystick {
    public Vector2 initPos;
    //idk if ill keep the touchPos but for now I'll leave it
    Vector2 touchPos;
    Color backStickColor = Color.WHITE;
    Color frontStickColor;
    Circle backStick;
    Circle frontStick;
    Circle backCheckCircle;

    int radius;

    public Joystick()
    {
    }

    public Joystick(Vector2 initPos, int radius, Color col)
    {
        init(initPos, radius, col);
    }

    public void init(Vector2 initPos, int radius, Color col)
    {
        this.initPos = initPos;
        this.radius = radius;
        backStick = new Circle(initPos,(float)radius);
        backCheckCircle = new Circle(initPos, (float) radius*1.3f);
        frontStickColor = col;
        backStickColor = new Color(frontStickColor);
        backStickColor.mul(.25f);
        frontStick = new Circle(initPos,(float)(radius*.5));
    }

    public void update(Vector2 touch)
    {
        if(isTouchingStick(touch))
        {
            Vector2 temp = touch.sub(initPos);
            float l = temp.len();
            Vector2 j = temp.nor().scl(Math.min(l, backStick.radius)).add(initPos);
            frontStick.setPosition(j);
        }

        //else{
        //    frontStick.setPosition(initPos);
        //}


    }

    public Vector2 distanceFromOrigin()
    {
        return new Vector2(frontStick.x-backStick.x, frontStick.y-backStick.y);
    }

    public boolean isTouchingStick(Vector2 touch)
    {
        return backCheckCircle.contains(touch);
    }

    public Vector2 getPosition()
    {
        return initPos;
    }

    public void setRadius(int rad)
    {
        radius = rad;
    }

    public void render(ShapeRenderer renderer) {
        renderer.set(ShapeType.Filled);
        //renderer.set();
        //draw the background of the stick first
        renderer.setColor(backStickColor);
        renderer.circle(backStick.x,backStick.y,backStick.radius);
        //draw the front of the joystick next
        renderer.set(ShapeType.Filled);
        renderer.setColor(frontStickColor);
        renderer.circle(frontStick.x,frontStick.y,frontStick.radius);
    }
}
