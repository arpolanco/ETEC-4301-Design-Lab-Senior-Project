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
    
    public final float maxThrottle = 100.0f; //based on joystick size on my desktop
    public final float maxRoll = 100.0f; //based on joystick size on my desktop
    public final float maxPitch = 100.0f; //based on joystick size on my desktop
    public final float maxYaw = 100.0f; //based on joystick size on my desktop
    
    final Random testData = new Random(); //delete this, for testing purposes

    boolean debugServ = false;

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

    public float getThrottle() {
        return testData.nextFloat() * maxThrottle; //for testing, pls delete
        //return throttle;
    }

    public void setThrottle(float throttle) {
        this.throttle = throttle;
    }

    public float getRoll() {
        return testData.nextFloat() * maxRoll; //for testing, pls delete
        //return roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public float getPitch() {
        return testData.nextFloat() * maxPitch; //for testing, pls delete
        //return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return testData.nextFloat() * maxYaw; //for testing, pls delete
        //return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}
