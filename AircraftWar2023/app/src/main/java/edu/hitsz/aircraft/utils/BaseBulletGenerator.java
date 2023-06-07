package edu.hitsz.aircraft.utils;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.utils.MotionInfo;

import java.util.List;
import java.util.function.Function;

/**
 * @author Chris
 */
public abstract class BaseBulletGenerator {

    protected int shootNum;
    public BaseBulletGenerator(int shootNum){
        this.shootNum = shootNum;
    }
    /**
     *
     * @param x
     * @param y
     * @param speed
     * @param getBullet
     * @return 运动信息List
     */
    public abstract List<BaseBullet> createBullets(int x, int y, int speed, Function<MotionInfo, BaseBullet> getBullet);
}