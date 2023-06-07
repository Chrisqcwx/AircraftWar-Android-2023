package edu.hitsz.prop;

import edu.hitsz.prop.factory.AbstractPropFactory;
import edu.hitsz.prop.factory.BloodPropFactory;
import edu.hitsz.prop.factory.BombPropFactory;
import edu.hitsz.prop.factory.FireBallPropFactory;
import edu.hitsz.prop.factory.FirePropFactory;
import edu.hitsz.prop.factory.RocketPropFactory;
import edu.hitsz.utils.RandomChoiceGenerator;

import java.util.LinkedList;
import java.util.List;

public class RandomPropGenerator extends RandomChoiceGenerator {

    private final List<AbstractPropFactory> propFactories = new LinkedList<>();

    /**
     * 根据权重选择道具
     * @param weights 加血道具、火力道具、炸弹道具、不生成道具的权重
     */
    public RandomPropGenerator(List<Float> weights) {
        super(weights);
        if (weights.size()>6) {
            throw new RuntimeException("The length of weights > 6");
        }

        propFactories.add(new BloodPropFactory());
        propFactories.add(new FirePropFactory());
        propFactories.add(new BombPropFactory());
        propFactories.add(new RocketPropFactory());
        propFactories.add(new FireBallPropFactory());

    }



    public BaseProp nextProp(int locationX, int locationY) {
        int choice = super.nextChoice();
//        BaseProp resProp = switch (choice) {
//            case 0, 1, 2 -> propFactories.get(choice).createProp(locationX, locationY);
//            default -> null;
//        };
        if (choice < 5 && choice >= 0) {
            return propFactories.get(choice).createProp(locationX, locationY);
        }
        return null;
    }
}
