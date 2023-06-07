package edu.hitsz.aircraft.enemyaircraft.factory;

import edu.hitsz.utils.RandomChoiceGenerator;

import java.util.List;

/**
 * @author Chris
 */
public class RandomEnemyFactoryGenerator extends RandomChoiceGenerator {
    /**
     * 根据权重选择敌机
     * @param weights 普通敌机、精英敌机、Boss机的权重
     */
    public RandomEnemyFactoryGenerator(List<Float> weights) {
        super(weights);
        if (weights.size()!=2) {
            throw new RuntimeException("The length of weights do not equal to 3");
        }
    }


    public AbstractEnemyFactory nextEnemyFactory(boolean createBoss) {
        if (createBoss) {
            return new BossEnemyFactory();
        }
        int choice = super.nextChoice();
        if(choice==1) {
            return new EliteEnemyFactory();
        }
        else{
            return new MobEnemyFactory();
        }
    }
}
