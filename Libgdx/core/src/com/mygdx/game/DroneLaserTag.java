package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.GUI.*;

import java.io.FileNotFoundException;
import java.io.IOException;

public class DroneLaserTag extends ScreenAdapter implements InputProcessor{

    public static final float WORLD_SIZE = 480.0f;
    private static final int BALL_COUNT = 250;
    private static final int OB_COUNT = 1;
    boolean buttonHeld = false;
    ShapeRenderer renderer;
    ExtendViewport viewport;
    //BouncingBall ball;
    Vector3 tp = new Vector3();
    Joystick j;
    GUILayout gui;
    boolean dragging;
    private Client client;
    boolean debugServClient = true;



    //ObjParser op = new ObjParser(new File("C:\\Users\\Dude XPS\\Documents\\Programming\\AI_Labs\\AI_Lab1 Game of Life - Copy\\core\\src\\maps\\map0.obj"));

    public DroneLaserTag() throws FileNotFoundException {
    }

    @Override
    public void show() {
        renderer = new ShapeRenderer();
        renderer.setAutoShapeType(true);
        viewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    }

    private void init() {
        gui = new GUILayout(viewport);
        Gdx.input.setInputProcessor(this);
        j = new Joystick(new Vector2((float)(viewport.getScreenWidth()*.5), (float)(viewport.getScreenHeight()*.5)), 100, Color.WHITE);
        if(debugServClient && client == null) {
            client = new Client();
            client.start();
        }

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        init();
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }

    @Override
    public void render(float delta) {
        viewport.apply();

        
        if(debugServClient) {
            if(client.sendData()){
                Gdx.gl.glClearColor(0, 0, 1, 1);
            }else{
                Gdx.gl.glClearColor(1, 0, 0, 1);
            }
            //get and draw video frame from server
            /*
            SpriteBatch batch = new SpriteBatch();
        
            batch.begin();
            batch.draw(client.getImage(), 100, 100);
            batch.end();
            */
            //I assume this runs at 60fps, so do this asyncronously?
         //   gui.render(image);
        }else{
            Gdx.gl.glClearColor(0, 0, 0, 1);
        }
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);


        renderer.setProjectionMatrix(viewport.getCamera().combined);
        renderer.begin(ShapeType.Filled);
        
        gui.update(tp);
        gui.render(renderer);

        renderer.end();
    }


    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button != Input.Buttons.LEFT || pointer > 0) return false;
        viewport.getCamera().unproject(tp.set(screenX, screenY, 0));
        dragging = true;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!dragging) return false;
        viewport.getCamera().unproject(tp.set(screenX, screenY, 0));
        System.out.println(tp.toString());
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
