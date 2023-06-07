package edu.hitsz.prop.factory;

import edu.hitsz.prop.BaseProp;
import edu.hitsz.prop.BombProp;
import edu.hitsz.prop.FireBallProp;

public class FireBallPropFactory extends AbstractPropFactory{
    @Override
    public BaseProp createProp(int locationX, int locationY) {
        return new FireBallProp(locationX, locationY, super.getSpeedX(), super.getSpeedY());
    }
}
