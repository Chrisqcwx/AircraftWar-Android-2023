package edu.hitsz.aircraft;

import edu.hitsz.aircraft.utils.BaseBulletGenerator;

/**
 * @author Chris
 */
public interface IHeroAircraftForProp {
    /**
    ** 加血道具调用
     */
    void increaseHp(int hp);
    /**
     ** 火力道具调用
     */
    void setShootStrategy(BaseBulletGenerator bulletGenerator);
    /**
     ** 炸弹道具调用
     */
//    void addBomb(int num);
    void addRocketBullet(int x, int y);

    void setIsFireBallBullet(boolean value);
}
