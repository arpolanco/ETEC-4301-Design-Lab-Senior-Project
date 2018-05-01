package com.mygdx.game;

import com.badlogic.gdx.math.Vector3;
import java.util.Random;

public class Drone {
    public Client client;
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
    
    public int previousThrottle;
    public int previousRoll;
    public int previousPitch;
    public int previousYaw;

    public void setTimeTillShoot(float timeTillShoot) {
        this.timeTillShoot = timeTillShoot;
    }
    byte throttleByte = 0;
    byte rollByte = 0;
    byte pitchByte = 0;
    byte yawByte = 0;
    private static Drone instance;
    
    final Random testData = new Random(); //delete this, for testing purposes

    public boolean isClientConnected = false;

    public Drone()
    {
        if(isClientConnected)
        {
            client = new Client();
            client.start();
        }

        game.initVals(game.gm, this);

    }

    public static Drone getInstance()
    {
        if (instance == null)
        {
            instance = new Drone();
        }
        return instance;
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
        if(timeTillShoot <= 0)
        {
            timeTillShoot = cooldown;
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
        throttle = (input + 1)/2.0f; //percentage of max
        throttleByte = (byte) THRUST;
        throttleByte |= (byte)(0x7e*throttle);
        return throttleByte;
    }

    public void setThrottle(float throttle) {
        this.throttle = throttle;
    }

    public byte getRoll(float input){
        roll = 1 - (input+1)/2.0f; //percentage of max
        rollByte = (byte) ROLL;
        rollByte |= (byte)(0xe*roll);
        return rollByte;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public byte getPitch(float input) {
        pitch = (input + 1)/2.0f; //percentage of max
        pitchByte = (byte) PITCH;
        pitchByte |= (byte)(0xe*pitch);
        return pitchByte;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public byte getYaw(float input) {
        yaw = (input + 1)/2.0f; //percentage of max
        yawByte = (byte) YAW;
        yawByte |= (byte)(0xe*yaw);
        return yawByte;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}
