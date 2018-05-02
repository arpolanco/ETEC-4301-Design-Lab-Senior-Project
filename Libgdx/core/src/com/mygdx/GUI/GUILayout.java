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

import java.util.HashMap;


public class GUILayout{

    private static final Color COLOR = Color.WHITE;
    Vector2 position;
    public Joystick leftJoystick;
    public Joystick rightJoystick;
    public Button shootButton;
    public Button quitButton;
    public Button flightButton;
    public Button ipButton;
    Texture quit;
    Texture fi;
    Texture ip;
    Texture shoot;
    StatusBar healthBar;
    StatusBar shootBar;
    int joyStickRad;
    Texture image;
    float guiWidth;
    float guiHeight;
    int buttonHeight;
    int buttonWidth;
    SpriteBatch batch;
    //viewport view;
    public boolean displayIPButton = true;




    public GUILayout(Viewport viewport) {
        init(viewport);

    }

    public GUILayout(Viewport viewport, Vector2 postion) {
        init(viewport);
    }

    public void init(Viewport viewport) {
        position = new Vector2();
        joyStickRad = (int)((viewport.getWorldWidth()* viewport.getWorldHeight()) *.00025);

        //sizes of buttons
        buttonHeight = (int)((viewport.getWorldWidth()* viewport.getWorldHeight()) *.0002);//(int)((viewport.getWorldWidth()* viewport.getWorldHeight()) *.01);
        buttonWidth = (int)(buttonHeight*1.5);

        leftJoystick = new Joystick(new Vector2((int)(viewport.getWorldWidth()*.25), (int)(viewport.getWorldHeight() *.3)), joyStickRad, Color.RED);
        leftJoystick.setSnapY(false);
        rightJoystick = new Joystick(new Vector2((int)(viewport.getWorldWidth()*.75), (int)(viewport.getWorldHeight() *.3)), joyStickRad, Color.WHITE);

        //buttons
        quitButton = new Button(new Vector2((int)(viewport.getWorldWidth()-buttonWidth), (int)(viewport.getWorldHeight()-buttonHeight)), new Vector2(buttonWidth, buttonHeight),"QUIT", false, Color.CHARTREUSE);
        flightButton = new Button(new Vector2((int)(viewport.getWorldWidth()-buttonWidth), (int)(viewport.getWorldHeight()-buttonHeight*2.1)), new Vector2(buttonWidth, buttonHeight),"FLIGHT", false,Color.CYAN);
        ipButton = new Button(new Vector2(((int)(viewport.getWorldWidth()*.5-buttonWidth)), (int)(viewport.getWorldHeight() *.5-buttonHeight)), new Vector2(buttonWidth*2, buttonHeight*2),"IP", true, Color.LIGHT_GRAY);
        shootButton = new Button(new Vector2((int)((viewport.getWorldWidth()*.5)-buttonWidth*.5), (int)(viewport.getWorldHeight()*.4+buttonHeight)),new Vector2(buttonWidth*1.2f, buttonHeight),"FIRE", false, Color.OLIVE);

        //health bars
        healthBar = new StatusBar(new Vector2(viewport.getWorldWidth()*.01f,(int)(viewport.getWorldHeight()*.95)), (int)(viewport.getWorldWidth()*.4), (int)(viewport.getWorldHeight()*.03), 100, Color.FIREBRICK);
        shootBar = new StatusBar(new Vector2(viewport.getWorldWidth()*.01f,(int)(viewport.getWorldHeight()*.9)), (int)(viewport.getWorldWidth()*.4), (int)(viewport.getWorldHeight()*.03), 100, Color.YELLOW);

        //gui size
        guiWidth = viewport.getWorldWidth();
        guiHeight = viewport.getWorldHeight();

        //textures
        quit = new Texture(Gdx.files.internal("Quit.png"));
        fi = new Texture(Gdx.files.internal("fi.png"));
        ip = new Texture(Gdx.files.internal("IP.png"));
        shoot = new Texture(Gdx.files.internal("Shoot.png"));

        batch = new SpriteBatch();

        //image = new Texture(Gdx.files.internal("badlogic.jpg"));
        //backgroundImg = new Rectangle();
        //backgroundImg.set((float)(viewport.getScreenWidth()*.5), image.getHeight()*.5f,image.getHeight(), image.getWidth());
    }



    public void update(HashMap<Integer, Vector2> touchlist) {

       if(!displayIPButton)
       {
           leftJoystick.update(touchlist);
           rightJoystick.update(touchlist);
       }

    }




    public void render(ShapeRenderer renderer) {

        //spriteBatch.draw(image,50, 50);
        batch.getProjectionMatrix().setToOrtho2D(0, 0, guiWidth, guiHeight);
        batch.begin();
        //System.out.println(batch.isDrawing())
        if(!displayIPButton)
        {
            leftJoystick.render(renderer);
            rightJoystick.render(renderer);
            healthBar.render(renderer);
            shootBar.render(renderer);
            //quitButton.render(renderer);
            //flightButton.render(renderer);
            //shootButton.render(renderer);
            batch.draw(shoot,shootButton.mPos.x,shootButton.mPos.y,buttonWidth,buttonHeight);
            batch.draw(quit, quitButton.mPos.x,quitButton.mPos.y,buttonWidth,buttonHeight);
            batch.draw(fi,flightButton.mPos.x,flightButton.mPos.y,buttonWidth,buttonHeight);
            //System.out.println((buttonWidth/guiWidth)*buttonWidth);//((buttonHeight*.5f)/guiHeight)*buttonHeight);
            //batch.draw(quit,quitButton.mPos.x,quitButton.mPos.y,(buttonWidth*.5f)/guiWidth,(buttonHeight*.5f)/guiHeight);
            //batch.draw(fi,flightButton.mPos.x,flightButton.mPos.y,(buttonWidth*.5f)/guiWidth,(buttonHeight*.5f)/guiHeight);
        }

        else
        {
            //ipButton.render(renderer);
            batch.draw(ip,ipButton.mPos.x,ipButton.mPos.y,ipButton.mSize.x,ipButton.mSize.y);
        }

        batch.end();
        //renderer.line(position, new Vector2((position.x+temp.x * 20), (position.y+temp.y * 20)));
    }

    public void imageFeed(Texture img) {
        image = img;
    }

    public void setDisplayIPButton(boolean bol)
    {
        displayIPButton = bol;
    }
}

