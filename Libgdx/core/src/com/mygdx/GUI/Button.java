package com.mygdx.GUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;


/**
 * Created by austin on 9/22/17.
 */

public class Button
{
    SpriteBatch mBatch;
    ShapeRenderer mShapeRenderer;
    String mText;
    Vector2 mPos;
    Vector2 mSize;
    Color mForegroundColor;
    BitmapFont mFont;

    public Button(Vector2 pos, Vector2 size, String text, Color color)
    {
        mPos = pos;
        mText = text;
        mSize = size;
        mForegroundColor = color;
        mBatch = new SpriteBatch();
        mShapeRenderer = new ShapeRenderer();
        mFont = new BitmapFont();
    }
    public void render()
    {
        mShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        mShapeRenderer.setColor(Color.WHITE);
        mShapeRenderer.rect(mPos.x, mPos.y, mSize.x, mSize.y);
        mShapeRenderer.setColor(mForegroundColor);
        mShapeRenderer.rect(mPos.x + 2, mPos.y + 2, mSize.x - 4, mSize.y - 4);
        mShapeRenderer.end();
        mBatch.begin();
        GlyphLayout layout = new GlyphLayout(mFont, mText);
        mBatch.end();
    }
}
