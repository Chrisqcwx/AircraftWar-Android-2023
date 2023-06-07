package edu.hitsz.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RandomChoiceGenerator {

    private final Random random;
    private final int choicesNum;
    private final List<Float> splitLine;

    public RandomChoiceGenerator(List<Float> weights) {
        this.choicesNum = weights.size();
        this.splitLine = new LinkedList<>();
        float sum = 0;
        for (Float weight : weights) {
            sum += weight;
            this.splitLine.add(sum);
        }
        for (int i = 0; i < this.splitLine.size(); i++) {
            this.splitLine.set(i, this.splitLine.get(i)/sum);
        }

        this.random = new Random();
    }

    /**
     * 根据权重返回选择
     */
    public int nextChoice() {
        Float w = random.nextFloat();
        for (int i = 0; i< splitLine.size();i++) {
            if (w < splitLine.get(i)) {
                return i;
            }
        }
        return this.choicesNum - 1;
    }
}
