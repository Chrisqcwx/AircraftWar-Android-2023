package edu.hitsz.prop;

import edu.hitsz.aircraft.IHeroAircraftForProp;
import edu.hitsz.aircraft.enemyaircraft.AbstractEnemyAircraft;
//import edu.hitsz.application.MusicThread;
//import edu.hitsz.application.MusicThreadSuppliers;
import edu.hitsz.game.GameMessage;
import edu.hitsz.utils.IObserver;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

public class BombProp extends BaseProp {


    public BombProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    private List<IObserver> observers = new LinkedList<>();

    public void addObserver(IObserver observer) {
        this.observers.add(observer);
    }

    public void notifyAllObservers() {
        observers.forEach(IObserver::update);
    }

    @Override
    public void effect(IHeroAircraftForProp aircraft, ConcurrentLinkedQueue<? extends IObserver> enemys, ConcurrentLinkedQueue<? extends IObserver> enemyBullets) {

//        System.out.println("BombSupply active!");

        enemys.forEach(this::addObserver);
        enemyBullets.forEach(this::addObserver);

        this.notifyAllObservers();
        this.vanish();
    }

    @Override
    public String getSoundName() {
        return GameMessage.object_bomb;
    }

//    @Override
//    public Supplier<MusicThread> getMusicThreadSupplier() {
//        return MusicThreadSuppliers.BOMB;
//    }
}
