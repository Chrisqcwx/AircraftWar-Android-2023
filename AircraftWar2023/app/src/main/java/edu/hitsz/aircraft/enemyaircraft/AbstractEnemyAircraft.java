package edu.hitsz.aircraft.enemyaircraft;

import edu.hitsz.activity.SelectGameModeActivity;
import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.ICreateReward;
import edu.hitsz.aircraft.utils.BaseBulletGenerator;
//import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.prop.BaseProp;
import edu.hitsz.utils.IObserver;
import edu.hitsz.utils.MotionInfo;

import java.util.List;

/**
 * @author Chris
 */
public abstract class AbstractEnemyAircraft extends AbstractAircraft implements ICreateReward, IObserver {

    protected int power;

    public AbstractEnemyAircraft(int locationX, int locationY, int speedX, int speedY, int hp, BaseBulletGenerator bulletGenerator) {
        super(locationX, locationY, speedX, speedY, hp, bulletGenerator);
    }

    protected BaseBullet getBullet(MotionInfo motionInfo) {
        return new EnemyBullet(motionInfo, power);
    }


    @Override
    public abstract int getScore();

    @Override
    public abstract List<BaseProp> getProps();

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= SelectGameModeActivity.screenHeight) {
            vanish();
        }
    }
}
