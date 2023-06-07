package edu.hitsz.prop.factory;

import edu.hitsz.prop.BaseProp;
import edu.hitsz.prop.FireProp;

public class FirePropFactory extends AbstractPropFactory {
    @Override
    public BaseProp createProp(int locationX, int locationY) {
        return new FireProp(locationX, locationY, super.getSpeedX(), super.getSpeedY());
    }
}
