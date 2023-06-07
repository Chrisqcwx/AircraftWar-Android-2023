package edu.hitsz.prop.factory;

import edu.hitsz.prop.BaseProp;
import edu.hitsz.prop.BloodProp;
import edu.hitsz.prop.RocketProp;


public class RocketPropFactory extends AbstractPropFactory {
    @Override
    public BaseProp createProp(int locationX, int locationY) {
        return new RocketProp(locationX, locationY, super.getSpeedX(), super.getSpeedY());
    }
}
