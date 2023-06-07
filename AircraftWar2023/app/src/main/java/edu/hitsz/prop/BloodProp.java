package edu.hitsz.prop;

import edu.hitsz.aircraft.IHeroAircraftForProp;
import edu.hitsz.aircraft.enemyaircraft.AbstractEnemyAircraft;
//import edu.hitsz.application.MusicThread;
//import edu.hitsz.application.MusicThreadSuppliers;
import edu.hitsz.game.GameMessage;
import edu.hitsz.utils.IObserver;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

public class BloodProp extends BaseProp {

    private int increaseBlood = 100;

    public BloodProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void effect(IHeroAircraftForProp aircraft, ConcurrentLinkedQueue<? extends IObserver> enemys, ConcurrentLinkedQueue<? extends IObserver> enemyBullets) {
        aircraft.increaseHp(increaseBlood);
        this.vanish();
//        System.out.println("BloodSupply active!");
    }

    @Override
    public String getSoundName() {
        return GameMessage.object_supply;
    }

//    @Override
//    public Supplier<MusicThread> getMusicThreadSupplier() {
//        return MusicThreadSuppliers.GET_SUPPLY;
//    }

}
