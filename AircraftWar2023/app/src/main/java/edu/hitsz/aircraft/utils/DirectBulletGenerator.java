package edu.hitsz.aircraft.utils;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.utils.MotionInfo;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DirectBulletGenerator extends BaseBulletGenerator {

    public DirectBulletGenerator(int shootNum){
        super(shootNum);
    }

    @Override
    public List<BaseBullet> createBullets(int x, int y, int speed, Function<MotionInfo, BaseBullet> getBullet) {
        if (shootNum == 0){
            return null;
        }
        return (IntStream.range(0, super.shootNum).mapToObj(i->
            new MotionInfo(x + (i*2 - super.shootNum + 1)*10, y, 0, speed)
        ).map(getBullet)).collect(Collectors.toList());
    }
}
