package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
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
import java.util.HashMap;

public class DroneLaserTag extends ScreenAdapter implements InputProcessor{
    ShapeRenderer renderer;
    ExtendViewport viewport;
    Vector3 tp = new Vector3();
    GUILayout gui;
    boolean dragging;
    Drone drone = new Drone();
    SpriteBatch spriteBatch;
    Texture testImg = new Texture(Gdx.files.internal("badlogic.jpg"));

    public static HashMap<Integer, Vector2> touchlist;
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
        //j = new Joystick(new Vector2((float)(viewport.getScreenWidth()*.5), (float)(viewport.getScreenHeight()*.5)), 100, Color.WHITE);
        spriteBatch = new SpriteBatch();
        touchlist = new HashMap<Integer, Vector2>();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        init();
    }

    @Override
    public void dispose() {
        renderer.dispose();
        spriteBatch.dispose();
    }

    @Override
    public void render(float delta) {
        viewport.apply();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);



        //spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        //spriteBatch.begin();
        renderer.setProjectionMatrix(viewport.getCamera().combined);
        renderer.begin(ShapeType.Filled);

        if(drone.isClientConnected){
            sendTelemetryByte();
        }else{
            Gdx.gl.glClearColor(0, 0, 0, 1);
        }
        gui.setDisplayIPButton(false);
        gui.imageFeed(testImg);
        gui.update(touchlist);
        gui.render(renderer);
        //Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(gui.displayIPButton == true)
        {
            //System.out.println("gui.displayIPButton");
            if(gui.ipButton.isPressed(new Vector2(tp.x, tp.y)))
            {
                if(gui.displayIPButton == true)
                {
                    if(gui.ipButton.isTextGiven == true)
                    {
                        drone.client = new Client();
                        drone.client.start();
                        drone.client.HOST = gui.ipButton.mText;
                        drone.client.openSocket();
                        if(drone.client.isConnected == false)
                            gui.ipButton.isTextGiven = false;
                        else
                            gui.setDisplayIPButton(false);
                    }
                }

            }
            else if(gui.ipButton.isTextGiven)
            {
                System.out.println("Connecting!");
                drone.client = new Client();
                drone.client.start();
                drone.client.HOST = gui.ipButton.mText;
                drone.client.openSocket();

                if(drone.client.isConnected == false) {
                    System.out.println("Connection failed!");
                    gui.ipButton.isTextGiven = false;
                    gui.ipButton.hasSubmitted = false;
                }
                else {
                    System.out.println("Connected!");
                    gui.setDisplayIPButton(false);
                    drone.isClientConnected = true;
                }
            }
        }


        renderer.end();
        drone.update(Gdx.graphics.getDeltaTime());
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
        /*if (button != Input.Buttons.LEFT || pointer > 0) return false;
        viewport.getCamera().unproject(tp.set(screenX, screenY, 0));
        dragging = true;
        return true;
        */
        viewport.getCamera().unproject(tp.set(screenX, screenY, 0));
        if(!touchlist.containsKey(pointer))touchlist.put(pointer, new Vector2(tp.x, tp.y));
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        /*tp = new Vector3();
        return false;
        */
        touchlist.remove(pointer);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
       /* if (!dragging) return false;
        viewport.getCamera().unproject(tp.set(screenX, screenY, 0));
        //System.out.println(tp.toString());
        return true;
        */
        viewport.getCamera().unproject(tp.set(screenX, screenY, 0));
        Vector2 touch=touchlist.get(pointer);
        if(touch!=null)touch.set(tp.x, tp.y);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
    
    public boolean sendTelemetryByte(){
        //left stick X: yaw
        //left stick Y: thrust
        //right stick X: roll
        //right stick Y: pitch
        //return if nothing changed
        //test quit
        if(gui.quitButton.isPressed(new Vector2(tp.x, tp.y))){
            return drone.client.sendByte((byte) drone.QUIT);
        }
        if(gui.flightButton.isPressed(new Vector2(tp.x, tp.y))){
            return drone.client.sendByte((byte) 'p');
        } 
        //test fire
        if(gui.shootButton.isPressed(new Vector2(tp.x, tp.y))){
            if(drone.canFire())
                return drone.client.sendByte((byte) drone.FIRE);
        }
        
        //float maxRange = 50.0f; //arbitrary from testing on desktop
        byte value;        
        //testing throttle
        value = drone.getThrottle(gui.leftJoystick.percentageDistanceFromRadius().y);
        if(value != drone.previousThrottle){
            
            drone.previousThrottle = value;
            return drone.client.sendByte(value);
        }
        
        //testing yaw
        value = drone.getYaw(gui.leftJoystick.percentageDistanceFromRadius().x);
        if(value != drone.previousYaw){
            drone.previousYaw = value;
            return drone.client.sendByte(value);
        }
        
        //testing roll
        value = drone.getRoll(gui.rightJoystick.percentageDistanceFromRadius().x);
        if(value != drone.previousRoll){
            drone.previousRoll = value;
            return drone.client.sendByte(value);
        }
        
        //testing pitch
        value = drone.getPitch(gui.rightJoystick.percentageDistanceFromRadius().y);
        if(value != drone.previousPitch){            
            drone.previousPitch = value;
            return drone.client.sendByte(value);
        }
        return true;
    }
}
