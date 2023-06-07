package edu.hitsz.aircraft.enemyaircraft;

import edu.hitsz.aircraft.utils.BaseBulletGenerator;
import edu.hitsz.ImageManager;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.prop.BaseProp;

import java.util.LinkedList;
import java.util.List;

/**
 * 普通敌机
 * 不可射击
 *
 * @author hitsz
 */
public class MobEnemy extends AbstractEnemyAircraft {

    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp, BaseBulletGenerator bulletGenerator) {
        super(locationX, locationY, speedX, speedY, hp, bulletGenerator);
    }

    @Override
    public int getScore() {
        return 10;
    }

    @Override
    public List<BaseProp> getProps() {
        return new LinkedList<>();
    }


    @Override
    public List<BaseBullet> shoot() {
        return new LinkedList<>();
    }

    @Override
    public void update() {
        new Thread(()->{
            this.image = ImageManager.MOB_DIE_ENEMY_IMAGE;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.vanish();
        }).start();
//        this.vanish();

    }
}
