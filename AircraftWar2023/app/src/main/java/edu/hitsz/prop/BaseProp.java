package edu.hitsz.prop;

import edu.hitsz.activity.SelectGameModeActivity;
import edu.hitsz.aircraft.IHeroAircraftForProp;
//import edu.hitsz.application.MusicThread;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.utils.IObserver;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class BaseProp extends AbstractFlyingObject {


    public BaseProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void forward() {
        super.forward();

        // 判定 y 轴出界
        if (speedY > 0 && locationY >= SelectGameModeActivity.screenHeight) {
            // 向下飞行出界
            vanish();
        }else if (locationY <= 0){
            // 向上飞行出界
            vanish();
        }
    }

    /**
     * 道具对Aircraft产生影响
     * @param aircraft 影响的aircraft
     */
    public abstract void effect(IHeroAircraftForProp aircraft, ConcurrentLinkedQueue<? extends IObserver> enemys, ConcurrentLinkedQueue<? extends IObserver> enemyBullets);

    public abstract String getSoundName();
}
