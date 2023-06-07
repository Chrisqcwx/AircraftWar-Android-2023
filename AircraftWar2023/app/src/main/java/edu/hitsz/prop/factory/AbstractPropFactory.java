package edu.hitsz.prop.factory;

import edu.hitsz.prop.BaseProp;
import edu.hitsz.prop.utils.Params;

public abstract class AbstractPropFactory {

    public abstract BaseProp createProp(int locationX, int locationY);

    protected int getSpeedX() {
        return Params.Init.SPEEDX;
    }

    protected int getSpeedY() {
        return Params.Init.SPEEDY;
    }
}
