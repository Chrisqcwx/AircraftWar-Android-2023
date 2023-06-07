package edu.hitsz.aircraft.enemyaircraft.factory;

import edu.hitsz.aircraft.enemyaircraft.AbstractEnemyAircraft;
import edu.hitsz.aircraft.enemyaircraft.MobEnemy;
import edu.hitsz.aircraft.utils.Params;

public class MobEnemyFactory extends AbstractEnemyFactory {


    public MobEnemyFactory() {
        speedX = Params.Init.SpeedX.MOB;
        speedY = Params.Init.SpeedY.MOB;
        hp = Params.Init.Hp.MOB;
    }

    @Override
    public AbstractEnemyAircraft createEnemy() {
        return new MobEnemy(super.getLocationX(), super.getLocationY(), speedX, speedY,hp, null);
    }
}
