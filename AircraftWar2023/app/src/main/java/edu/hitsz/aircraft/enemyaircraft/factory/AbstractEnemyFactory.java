package edu.hitsz.aircraft.enemyaircraft.factory;

import edu.hitsz.activity.SelectGameModeActivity;
import edu.hitsz.aircraft.enemyaircraft.AbstractEnemyAircraft;
import edu.hitsz.ImageManager;
//import edu.hitsz.application.Main;


/**
 * @author Chris
 */
public abstract class AbstractEnemyFactory {

    protected int speedX;
    protected int speedY;
    protected int hp;

    public abstract AbstractEnemyAircraft createEnemy();

    public void increaseSpeed(int increaseSpeedX, int increaseSpeedY) {
        this.speedX += increaseSpeedX;
        this.speedY += increaseSpeedY;
    }

    public void increaseHp(int increase) {
        this.hp += increase;
    }

    protected int getLocationX() {
        return (int) (Math.random() * (SelectGameModeActivity.screenWidth - ImageManager.MOB_ENEMY_IMAGE.getWidth()));
    }

    protected int getLocationY() {
        return (int) (Math.random() * SelectGameModeActivity.screenHeight * 0.05);
    }
}
