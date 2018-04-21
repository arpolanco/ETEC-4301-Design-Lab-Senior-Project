package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import java.util.Random;

public class Drone {
    Client client;
    GameMode game = new GameMode("Game1");
    float health;
    public float timeTillShoot;
    float cooldown; //amount of time before can shoot again
    float maxShots;


    float damage;
    float throttle; //this is just a value how fast you are moving forward or backward
    float roll; //this is an angle
    float pitch; //
    float yaw; //
    Vector3 position;
    String playerID;
    int score;
    
    final int THRUST = 0x80;    //0b10 000000;
    final int QUIT = 0x40;   //0b01 000000;
    final int FIRE = 0x30;   //0b00 11 0000;
    final int PITCH = 0x00;   //0b00 00 0000;
    final int ROLL = 0x10;   //0b00 01 0000;
    final int YAW = 0x20;    //0b00 10 0000;
    
    public final float maxThrottle = 100.0f; //based on joystick size on my desktop
    public final float maxRoll = 100.0f; //based on joystick size on my desktop
    public final float maxPitch = 100.0f; //based on joystick size on my desktop
    public final float maxYaw = 100.0f; //based on joystick size on my desktop
    public int previousThrottle;
    public int previousRoll;
    public int previousPitch;
    public int previousYaw;
    byte throttleByte = 0;
    byte rollByte = 0;
    byte pitchByte = 0;
    byte yawByte = 0;
    
    final Random testData = new Random(); //delete this, for testing purposes

    boolean debugServ = true;

    public Drone()
    {
        if(debugServ)
        {
            client = new Client();
            client.start();
        }

        game.initVals(game.gm, this);

    }


    public void getMovementInput()
    {
        //need to grab from gui input
    }

    public Vector3 getPosition()
    {
        return position;
    }

    public void updateHealth(float deltaHP)
    {
        health += deltaHP;
    }

    public String getHealthValue()
    {
        System.out.println(health);
        return Float.toString(health);
    }

    public boolean canFire()
    {
        if(cooldown <= 0)
        {
            return true;
        }

        else
            return false;
    }

    public void update(float dt)
    {
        if(timeTillShoot > 0)
        {
            timeTillShoot -= dt;
            if (timeTillShoot <= 0)
                timeTillShoot = 0;
        }
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getMaxShots() {
        return maxShots;
    }

    public void setMaxShots(float maxShots) {
        this.maxShots = maxShots;
    }
    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }


    public float getCooldown() {
        return cooldown;
    }

    public void setCooldown(float cooldown) {
        this.cooldown = cooldown;
    }

    public byte getThrottle(float input) {
        throttle = input;
        throttle /= maxThrottle;
        throttleByte = (byte) THRUST;
        throttleByte |= (byte)(0x7f*throttle);
        return throttleByte;
    }

    public void setThrottle(float throttle) {
        this.throttle = throttle;
    }

    public byte getRoll(float input) {
        roll = input;
        roll /= maxRoll;
        rollByte = (byte) ROLL;
        rollByte |= (byte)(0xf*roll);
        return rollByte;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public byte getPitch(float input) {
        pitch = input;
        pitch /= maxPitch;
        pitchByte = (byte) PITCH;
        pitchByte |= (byte)(0xf*pitch);
        return pitchByte;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public byte getYaw(float input) {
        yaw = input;
        yaw /= maxYaw;
        yawByte = (byte) YAW;
        yawByte |= (byte)(0xf*yaw);
        return yawByte;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}
