package edu.hitsz.bullet;

import edu.hitsz.activity.SelectGameModeActivity;
import edu.hitsz.utils.IObserver;
import edu.hitsz.utils.MotionInfo;
//import edu.hitsz.application.Main;
import edu.hitsz.basic.AbstractFlyingObject;

/**
 * 子弹类。
 * 也可以考虑不同类型的子弹
 *
 * @author hitsz
 */
public abstract class BaseBullet extends AbstractFlyingObject implements IObserver {

    private int power = 10;

    public BaseBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY);
        this.power = power;
    }

    public BaseBullet(MotionInfo motionInfo, int power) {
        this(motionInfo.getX(), motionInfo.getY(), motionInfo.getSpeedX(), motionInfo.getSpeedY(), power);
    }

    @Override
    public void forward() {
        super.forward();

        // 判定 x 轴出界
        if (locationX <= 0 || locationX >= SelectGameModeActivity.screenWidth) {
            vanish();
        }

        // 判定 y 轴出界
        if (speedY > 0 && locationY >= SelectGameModeActivity.screenHeight ) {
            // 向下飞行出界
            vanish();
        }else if (locationY <= 0){
            // 向上飞行出界
            vanish();
        }
    }

    public int getPower() {
        return power;
    }
}
