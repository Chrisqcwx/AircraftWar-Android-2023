package edu.hitsz.prop.factory;

import edu.hitsz.prop.BaseProp;
import edu.hitsz.prop.BombProp;

public class BombPropFactory extends AbstractPropFactory {
    @Override
    public BaseProp createProp(int locationX, int locationY) {
        return new BombProp(locationX, locationY, super.getSpeedX(), super.getSpeedY());
    }
}
