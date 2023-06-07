package edu.hitsz.aircraft.enemyaircraft.factory;

import edu.hitsz.aircraft.enemyaircraft.AbstractEnemyAircraft;
import edu.hitsz.aircraft.enemyaircraft.BossEnemy;
import edu.hitsz.aircraft.utils.Params;
import edu.hitsz.aircraft.utils.RoundBulletGenerator;

public class BossEnemyFactory extends AbstractEnemyFactory {



    public BossEnemyFactory() {
        speedX = Params.Init.SpeedX.BOSS;
        speedY = Params.Init.SpeedY.BOSS;
        hp = Params.Init.Hp.BOSS;
    }


    @Override
    public AbstractEnemyAircraft createEnemy() {
        System.out.println("BOSS 血量: " + hp);
        return new BossEnemy(super.getLocationX(), super.getLocationY()+25, speedX, speedY, hp,
                new RoundBulletGenerator(Params.Init.ShootNum.BOSS));
    }
}
