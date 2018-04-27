package com.mygdx.GUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.sun.org.apache.bcel.internal.generic.RET;


/**
 * Created by austin on 9/22/17.
 */

public class Button
{
    String mText;
    public Vector2 mPos;
    Vector2 mSize;
    Color mForegroundColor;
    //BitmapFont mFont;
    Rectangle button;
    boolean triggered = false;

    public Button(Vector2 pos, Vector2 size, String text, Color color)
    {
        mPos = pos;
        mText = text;
        mSize = size;
        mForegroundColor = color;
        mForegroundColor.a =.05f;
        button = new Rectangle(pos.x,pos.y,size.x,size.y);
        //mFont = new BitmapFont();

    }

    public boolean isPressed(Vector2 touch)
    {

        if(button.contains(touch))
        {
            if(!triggered)
            {
                triggered = true;
                System.out.println(mText);
                return true;
            }else{
                return false;
            }

            
        }else{
            triggered = false;
            return false;
        }

    }

    public void render(ShapeRenderer renderer) {
        renderer.set(ShapeRenderer.ShapeType.Filled);
        //renderer.set();
        //draw the background of the stick first
        renderer.setColor(mForegroundColor);
        renderer.rect(button.x,button.y,button.getWidth(),button.getHeight());

        /*
        mBatch.begin();
        GlyphLayout layout = new GlyphLayout(mFont, mText);
        mBatch.end();
        */
    }
}
