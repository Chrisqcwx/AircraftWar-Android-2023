package edu.hitsz.aircraft;

import edu.hitsz.prop.ICreateProb;

/**
 * @author Chris
 */
public interface ICreateReward extends ICreateProb {
    /**
     * @return 返回该敌机的得分
     */
    int getScore();
}
