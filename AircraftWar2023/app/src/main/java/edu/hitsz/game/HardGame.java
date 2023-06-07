package edu.hitsz.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;

import java.util.Arrays;
import java.util.List;

import edu.hitsz.ImageManager;
import edu.hitsz.R;
import edu.hitsz.utils.Counter;
import edu.hitsz.utils.params.GameMode;

@SuppressLint("ViewConstructor")
public class HardGame extends BaseGame{
    public HardGame(Context context, Handler handler, boolean isOnline) {
        super(context, handler, isOnline);
        this.backGround = ImageManager.BACKGROUND3_IMAGE;
    }

    private int enemyShootCycleNum = 35;
    private int heroShootCycleNum = 35;
    private int createEnemyCycleNum = 35;
    private final Counter enemyShootCycleUpdateCounter = new Counter(20);
    private final Counter heroShootCycleUpdateCounter = new Counter(16);
    private final Counter createEnemyCycleUpdateCounter = new Counter(15);

    @Override
    protected int getEnemyShootCycleNum() {
        return enemyShootCycleNum;
    }

    @Override
    protected int getHeroShootCycleNum() {
        return heroShootCycleNum;
    }

    @Override
    protected int getCreateEnemyCycleNum() {
        return createEnemyCycleNum;
    }

    @Override
    protected void heroShootUpdate() {
        if (heroShootCycleUpdateCounter.inc()) {
            if (heroShootCycleNum > 18) {
                heroShootCycleNum -= 3;
                System.out.printf("英雄机射击速度提升, 射击间隔%dms\n", getTimeInterval()*heroShootCycleNum);
            }
        }
    }

    @Override
    protected void enemyShootUpdate() {
        if (enemyShootCycleUpdateCounter.inc()) {
            if (enemyShootCycleNum > 18) {
                enemyShootCycleNum -= 3;
                System.out.printf("敌机射击速度提升, 射击间隔%dms\n", getTimeInterval()*enemyShootCycleNum);
            }
        }
    }

    @Override
    protected void createEnemyUpdate() {
        if (createEnemyCycleUpdateCounter.inc()) {
            if (createEnemyCycleNum > 18) {
                createEnemyCycleNum -= 3;
                increaseEnemyHp += 20;
                enemyWeights.set(0, enemyWeights.get(0)-3);
                enemyWeights.set(1, enemyWeights.get(1)+3);
                System.out.printf("精英敌机生成概率增加 = %.3f%%\n", enemyWeights.get(1)*100/(enemyWeights.get(0)+enemyWeights.get(1)));
                System.out.printf("敌机生成速度提升, 生成间隔%dms\n", getTimeInterval()*createEnemyCycleNum);
                System.out.println("敌机血量增加 20");
            }
        }
    }

    private List<Float> enemyWeights = Arrays.asList(70f, 30f);

    private int increaseEnemyHp = 0;

//    private int cycleDuration = 550;
//    private int cycleCnt = 0;
//
//    @Override
//    protected void newCycle() {
//        cycleCnt ++;
//        if (cycleCnt == 20) {
//            cycleCnt = 0;
//            if (cycleDuration > 200) {
//                cycleDuration -= 40;
//            }
//            increaseEnemyHp += 60;
//            enemyWeights.set(0, enemyWeights.get(0)-3);
//            enemyWeights.set(1, enemyWeights.get(1)+3);
//            System.out.println("********************游戏难度增加!*************");
//            System.out.println("敌机生成/射击间隔: "+cycleDuration);
//            System.out.println("敌机血量增加 60");
//            System.out.printf("精英敌机生成概率 = %.3f%%\n", enemyWeights.get(1)*100/(enemyWeights.get(0)+enemyWeights.get(1)));
//        }
//    }


    @Override
    protected boolean canCreateBoss() {
        return true;
    }

    @Override
    protected int getIncreaseBossHp() {
        return 300;
    }

    @Override
    protected int getIncreaseEnemyHp() {
        return increaseEnemyHp;
    }

    @Override
    protected int getGameId() {
        return GameMode.HARD;
    }

    @Override
    protected int getTimeInterval() {
        return 12;
    }

    @Override
    protected int getEnemyMaxNumber() {
        return 5;
    }

//    @Override
//    protected int getCycleDuration() {
//        return cycleDuration;
//    }

    @Override
    protected List<Float> getRandomWeights() {

        return enemyWeights;
//        return Arrays.asList(70f, 30f);
    }

}
