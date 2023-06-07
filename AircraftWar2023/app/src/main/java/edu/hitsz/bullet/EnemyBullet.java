package edu.hitsz.bullet;

import edu.hitsz.utils.MotionInfo;

/**
 * @Author hitsz
 */
public class EnemyBullet extends BaseBullet {

    public EnemyBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY, power);
    }

    public EnemyBullet(MotionInfo motionInfo, int power) {
        super(motionInfo, power);
    }

    @Override
    public void update() {
        this.vanish();
    }
}
