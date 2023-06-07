package edu.hitsz.prop.factory;

import edu.hitsz.prop.BaseProp;
import edu.hitsz.prop.BloodProp;

public class BloodPropFactory extends AbstractPropFactory {
    @Override
    public BaseProp createProp(int locationX, int locationY) {
        return new BloodProp(locationX, locationY, super.getSpeedX(), super.getSpeedY());
    }
}
