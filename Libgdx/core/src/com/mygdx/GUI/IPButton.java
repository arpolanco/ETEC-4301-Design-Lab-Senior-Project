package com.mygdx.GUI;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Drone;
import com.mygdx.game.DroneLaserTag;
import com.badlogic.gdx.Game;
import com.mygdx.game.Main;


public class IPButton extends ApplicationAdapter implements Input.TextInputListener
{
    public String mText;
    public Vector2 mPos;
    Vector2 mSize;
    Color mForegroundColor;
    BitmapFont mFont;
    Rectangle button;
    boolean triggered = false;
    boolean isTextInput = false;


    public IPButton(Vector2 pos, Vector2 size, String text, boolean textInput, Color color)
    {
        mPos = pos;
        mText = text;
        mSize = size;
        mForegroundColor = color;

        button = new Rectangle(pos.x,pos.y,size.x,size.y);
        isTextInput = textInput;
        mFont = new BitmapFont();
        mFont.setColor(new Color(255-color.r, 255-color.g, color.b, 255));


    }

    public boolean isPressed(Vector2 touch)
    {

        if(button.contains(touch))
        {
            System.out.println(mText);
            if(!triggered)
            {
                triggered = true;
                System.out.println(mText);
                if(isTextInput)
                    Gdx.input.getTextInput(this,"IP Input", mText, "Enter your IP here");

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
        //draw the background of the stick first
        renderer.setColor(mForegroundColor);
        renderer.rect(button.x,button.y,button.getWidth(),button.getHeight());
        //batch.begin();
        //mFont.draw(batch, mText, mPos.x, mPos.y+(mSize.y*.5f));
        //batch.end();
    }

    @Override
    public void input(String text) {
        mText = text;
        Main.getInstance().monch(1);
    }


    @Override
    public void canceled() {

    }
}
