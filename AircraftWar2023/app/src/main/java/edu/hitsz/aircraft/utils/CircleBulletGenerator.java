package edu.hitsz.aircraft.utils;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.utils.MotionInfo;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CircleBulletGenerator  extends BaseBulletGenerator {


    public CircleBulletGenerator(int shootNum) {
        super(shootNum);
    }
    @Override
    public List<BaseBullet> createBullets(int x, int y, int speed, Function<MotionInfo, BaseBullet> getBullet) {
        if (shootNum == 0) {
            return new LinkedList<>();
        }
        double deltaTheta = 2*Math.PI / (shootNum);
        return (IntStream.range(0, super.shootNum).asDoubleStream().map(i ->
                -Math.PI  + deltaTheta * (i + 1)
        ).mapToObj(theta->
                new MotionInfo(x, y, (int) (Math.sin(theta) * speed), (int) (Math.cos(theta) * speed))
        ).map(getBullet).collect(Collectors.toList()));
    }

}
