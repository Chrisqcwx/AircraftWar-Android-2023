package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.aircraft.IHeroAircraftForProp;
import edu.hitsz.game.GameMessage;
import edu.hitsz.utils.IObserver;
import edu.hitsz.utils.params.swingParams.Windows;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

public class RocketProp extends BaseProp {

    private boolean isActive = false;
    private int edge;
    private IHeroAircraftForProp heroAircraft;

    @Override
    public void forward() {
        super.forward();
        if (isActive && this.locationY < edge) {
//            System.out.println("aaaaaaaa");
//            if (heroAircraft == null) {
//                System.out.println("bbbbbbbbbb");
//            }
            heroAircraft.addRocketBullet(this.locationX, this.locationY);
            this.vanish();
        }
    }

    public RocketProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void effect(IHeroAircraftForProp aircraft, ConcurrentLinkedQueue<? extends IObserver> enemys, ConcurrentLinkedQueue<? extends IObserver> enemyBullets) {
        this.speedY = -10;
        isActive = true;
        edge = this.locationY / 2;
        heroAircraft = aircraft;
    }

    @Override
    public String getSoundName() {
        return GameMessage.object_supply;
    }

}
