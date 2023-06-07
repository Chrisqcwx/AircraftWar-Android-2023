package edu.hitsz.bullet;

import edu.hitsz.utils.MotionInfo;

/**
 * @Author hitsz
 */
public class HeroBullet extends BaseBullet {

    public HeroBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY, power);
    }

    public HeroBullet(MotionInfo motionInfo, int power) {
        super(motionInfo, power);
    }

    @Override
    public void update() {

    }
}
