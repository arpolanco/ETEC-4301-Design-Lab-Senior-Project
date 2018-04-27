package com.mygdx.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;


public class GUILayout{

    private static final Color COLOR = Color.WHITE;
    Vector2 position;
    public Joystick leftJoystick;
    public Joystick rightJoystick;
    public Button shootButton;
    public Button quitButton;
    StatusBar healthBar;
    StatusBar shootBar;
    int joyStickRad;
    Texture image;
    float guiWidth;
    float guiHeight;
    int buttonHeight;
    int buttonWidth;




    public GUILayout(Viewport viewport) {
        init(viewport);

    }

    public GUILayout(Viewport viewport, Vector2 postion) {
        init(viewport);
    }

    public void init(Viewport viewport) {
        position = new Vector2();
        joyStickRad = (int)((viewport.getWorldWidth()* viewport.getWorldHeight()) *.00025);
        buttonHeight = (int)((viewport.getWorldWidth()* viewport.getWorldHeight()) *.0002);//(int)((viewport.getWorldWidth()* viewport.getWorldHeight()) *.01);
        buttonWidth = (int)(buttonHeight*2);
        leftJoystick = new Joystick(new Vector2((int)(viewport.getWorldWidth()*.25), (int)(viewport.getWorldHeight() *.3)), joyStickRad, Color.RED);
        rightJoystick = new Joystick(new Vector2((int)(viewport.getWorldWidth()*.75), (int)(viewport.getWorldHeight() *.3)), joyStickRad, Color.WHITE);
        quitButton = new Button(new Vector2((int)(int)(viewport.getWorldWidth()*.7), (int)(viewport.getWorldHeight() *.9)), new Vector2(buttonWidth, buttonHeight),"QUIT", Color.CHARTREUSE);
        shootButton = new Button(new Vector2((int)((leftJoystick.initPos.x+rightJoystick.initPos.x*.5)-buttonWidth), (int)(viewport.getWorldHeight()*.4)),new Vector2(buttonWidth, buttonHeight),"FIRE", Color.OLIVE);
        healthBar = new StatusBar(new Vector2(viewport.getWorldWidth()*.01f,(int)(viewport.getWorldHeight()*.95)), (int)(viewport.getWorldWidth()*.4), (int)(viewport.getWorldHeight()*.03), 100, Color.FIREBRICK);
        shootBar = new StatusBar(new Vector2(viewport.getWorldWidth()*.01f,(int)(viewport.getWorldHeight()*.9)), (int)(viewport.getWorldWidth()*.4), (int)(viewport.getWorldHeight()*.03), 100, Color.YELLOW);
        guiWidth = viewport.getWorldWidth();
        guiHeight = viewport.getWorldHeight();

        //image = new Texture(Gdx.files.internal("badlogic.jpg"));
        //backgroundImg = new Rectangle();
        //backgroundImg.set((float)(viewport.getScreenWidth()*.5), image.getHeight()*.5f,image.getHeight(), image.getWidth());
    }



    public void update(Vector3 input) {
        Vector2 temp = new Vector2(input.x,input.y);
        leftJoystick.update(temp);
        rightJoystick.update(temp);


        /*if(quitButton.isPressed(temp))
        {
            System.out.println("Quit");
        }

        if(shootButton.isPressed(new Vector2(input.x,input.y)))
        {
            System.out.println("Shoot");
        }
        */


    }




    public void render(ShapeRenderer renderer, SpriteBatch spriteBatch ) {
        //HealthBar.render(renderer);
        //ShootBar.render(renderer);
        spriteBatch.begin();
        //change the image position
        //spriteBatch.draw(image,50, 50);
        leftJoystick.render(renderer);
        rightJoystick.render(renderer);
        healthBar.render(renderer);
        shootBar.render(renderer);
        quitButton.render(renderer);
        shootButton.render(renderer);
        //renderer.line(position, new Vector2((position.x+temp.x * 20), (position.y+temp.y * 20)));
    }

    public void imageFeed(Texture img) {
        image = img;
    }
}

