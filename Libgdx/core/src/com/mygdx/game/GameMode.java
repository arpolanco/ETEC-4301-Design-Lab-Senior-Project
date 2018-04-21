package com.mygdx.game;

/**
 * Created by Dude XPS on 4/20/2018.
 */

public class GameMode {
    public float health;
    public float damage;
    public int maxShots;
    public float cooldown;
    boolean powerups = false; //this was meant for AR
    public String gm;

    public GameMode(String game)
    {
        gm = game;
    }
    public void initVals(String game, Drone drone)
    {
        if(game == "Game1")
        {
            health = 100f;
            damage = 25;
            maxShots = 100;
            cooldown = 1f;

        }



        drone.setHealth(health);
        drone.setCooldown(cooldown);
        drone.setDamage(damage);
        drone.setMaxShots(maxShots);


    }

    public void initDrone(Drone drone)
    {

    }
}
