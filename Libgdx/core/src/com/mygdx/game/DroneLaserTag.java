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

public class DroneLaserTag extends ScreenAdapter implements InputProcessor{

    public static final float WORLD_SIZE = 480.0f;
    boolean buttonHeld = false;
    ShapeRenderer renderer;
    ExtendViewport viewport;
    //BouncingBall ball;
    Vector3 tp = new Vector3();
    //Joystick j;
    GUILayout gui;
    boolean dragging;
    private Client client;
    Drone drone = new Drone();
    SpriteBatch spriteBatch;
    Texture testImg = new Texture(Gdx.files.internal("badlogic.jpg"));
    
    boolean debugServClient = true;
    
    final int THRUST = 0x80;    //0b10 000000;
    final int QUIT = 0x40;   //0b01 000000;
    final int FIRE = 0x30;   //0b00 11 0000;
    final int PITCH = 0x00;   //0b00 00 0000;
    final int ROLL = 0x10;   //0b00 01 0000;
    final int YAW = 0x20;    //0b00 10 0000;

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
        if(debugServClient && client == null) {
            client = new Client();
            client.start();
        }
        spriteBatch = new SpriteBatch();

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

        if(debugServClient) {
            if(sendTelemetryByte()){
                Gdx.gl.glClearColor(0, 0, 1, 1);
            }else{
                Gdx.gl.glClearColor(1, 0, 0, 1);
            }
            //get and draw video frame from server

            /*
            batch.begin();
            batch.draw(client.getImage(), 100, 100);
            batch.end();
            */
            //I assume this runs at 60fps, so do this asyncronously?
         //   gui.render(image);
        }else{
            Gdx.gl.glClearColor(0, 0, 0, 1);
        }
        gui.imageFeed(testImg);
        gui.update(tp);
        //spriteBatch.begin();
        gui.render(renderer, spriteBatch);
        //spriteBatch.begin();
        //spriteBatch.draw(, 100, 100);
        //spriteBatch.end();
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gui.update(tp);
        //gui.render(renderer);


        //gui.render(renderer);

        spriteBatch.end();
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
        //System.out.println(tp.toString());
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
    
    public boolean sendTelemetryByte(){
        //left stick X: yaw
        //left stick Y: thrust
        //right stick X: pitch
        //right stick Y: roll
        //return if nothing changed
        
        //todo: have fire and quit buttons, prioritize them if press detected
        byte telemetry = 0;
        int action = THRUST; //just testing ok. in the future, do send anytime joystick data changes
        float maxRange = 50.0f; //arbitrary from testing on desktop
        switch (action){ //need proper way to define this
            case THRUST:
                telemetry |= THRUST;
                float throttle = gui.leftJoystick.distanceFromOrigin().y + maxRange;
                throttle /= drone.maxThrottle;
                telemetry |= (byte)(0x7f*throttle); //0b01111111;
                break;
            case YAW:
                telemetry |= YAW;
                float yaw = gui.leftJoystick.distanceFromOrigin().x + maxRange;
                yaw /= drone.maxYaw;
                telemetry |= (byte)(0xf*yaw); //0b00001111
                break;            
            case ROLL:
                telemetry |= ROLL;
                float roll = gui.rightJoystick.distanceFromOrigin().y + maxRange;
                roll /= drone.maxRoll;
                telemetry |= (byte)(0xf*roll); //0b00001111
                break;
            case PITCH:
                telemetry |= PITCH;
                float pitch = gui.rightJoystick.distanceFromOrigin().x + maxRange;
                pitch /= drone.maxPitch;
                telemetry |= (byte)(0xf*pitch); //0b00001111
                break;
            case FIRE:
                telemetry |= FIRE;
                break;
            case QUIT:
                telemetry |= QUIT;
                System.exit(0); //shut down app
                break;
        }
        return client.sendByte(telemetry);
    }
}
