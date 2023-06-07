package edu.hitsz.bullet;

import edu.hitsz.utils.MotionInfo;

public class HeroFireBallBullet extends BaseBullet {

    public HeroFireBallBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY, power);
    }

    public HeroFireBallBullet(MotionInfo motionInfo, int power) {
        super(motionInfo, power);
    }

    @Override
    public void update() {

    }

}
