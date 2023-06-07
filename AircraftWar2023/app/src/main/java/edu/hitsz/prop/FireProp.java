package edu.hitsz.prop;

import edu.hitsz.aircraft.IHeroAircraftForProp;
import edu.hitsz.aircraft.enemyaircraft.AbstractEnemyAircraft;
import edu.hitsz.aircraft.utils.DirectBulletGenerator;
import edu.hitsz.aircraft.utils.RoundBulletGenerator;
//import edu.hitsz.application.MusicThread;
//import edu.hitsz.application.MusicThreadSuppliers;
import edu.hitsz.game.GameMessage;
import edu.hitsz.utils.IObserver;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

public class FireProp extends BaseProp {
    private final int bulletNum = 5;

    public FireProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    private static Integer cnt=0;

    @Override
    public void effect(IHeroAircraftForProp aircraft, ConcurrentLinkedQueue<? extends IObserver> enemys, ConcurrentLinkedQueue<? extends IObserver> enemyBullets) {

//        System.out.println("FireSupply active!");
        Runnable r = () -> {
            synchronized (cnt) {
                cnt++;
            }
            aircraft.setShootStrategy(new RoundBulletGenerator(bulletNum));
            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (cnt == 1) {
                aircraft.setShootStrategy(new DirectBulletGenerator(1));
            }
//            System.out.println(cnt);
            synchronized (cnt) {
                cnt--;
            }
        };

        (new Thread(r)).start();
        this.vanish();
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
