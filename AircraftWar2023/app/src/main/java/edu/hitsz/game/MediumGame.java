package edu.hitsz.game;

import android.content.Context;
import android.os.Handler;

import java.util.Arrays;
import java.util.List;

import edu.hitsz.ImageManager;
import edu.hitsz.R;
import edu.hitsz.utils.Counter;
import edu.hitsz.utils.params.GameMode;

public class MediumGame extends BaseGame{
    public MediumGame(Context context, Handler handler, boolean isOnline) {
        super(context, handler, isOnline);
        this.backGround = ImageManager.BACKGROUND2_IMAGE;
        enemyWeights = Arrays.asList(70f, 30f);
    }

    private int increaseEnemyHp = 0;
    private final List<Float> enemyWeights;


    @Override
    protected int getTimeInterval() {
        return 15;
    }

    private int enemyShootCycleNum = 40;
    private int heroShootCycleNum = 40;
    private int createEnemyCycleNum = 40;
    private final Counter enemyShootCycleUpdateCounter = new Counter(30);
    private final Counter heroShootCycleUpdateCounter = new Counter(25);
    private final Counter createEnemyCycleUpdateCounter = new Counter(25);

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
            if (heroShootCycleNum > 25) {
                heroShootCycleNum -= 3;
                System.out.printf("英雄机射击速度提升, 射击间隔%dms\n", getTimeInterval()*heroShootCycleNum);
            }
        }
    }

    @Override
    protected void enemyShootUpdate() {
        if (enemyShootCycleUpdateCounter.inc()) {
            if (enemyShootCycleNum > 25) {
                enemyShootCycleNum -= 3;
                System.out.printf("敌机射击速度提升, 射击间隔%dms\n", getTimeInterval()*enemyShootCycleNum);
            }
        }
    }

    @Override
    protected void createEnemyUpdate() {
        if (createEnemyCycleUpdateCounter.inc()) {
            if (createEnemyCycleNum > 25) {
                createEnemyCycleNum -= 3;
                increaseEnemyHp += 15;
                enemyWeights.set(0, enemyWeights.get(0)-2);
                enemyWeights.set(1, enemyWeights.get(1)+2);
                System.out.printf("精英敌机生成概率增加 = %.3f%%\n", enemyWeights.get(1)*100/(enemyWeights.get(0)+enemyWeights.get(1)));
                System.out.printf("敌机生成速度提升, 生成间隔%dms\n", getTimeInterval()*createEnemyCycleNum);
                System.out.println("敌机血量增加 15");
            }
        }
    }

    @Override
    protected int getEnemyMaxNumber() {
        return 4;
    }


    @Override
    protected List<Float> getRandomWeights() {
        return enemyWeights;
    }


    @Override
    protected boolean canCreateBoss() {
        return true;
    }

    @Override
    protected int getIncreaseBossHp() {
        return 0;
    }

    @Override
    protected int getIncreaseEnemyHp() {
        return increaseEnemyHp;
    }

    @Override
    protected int getGameId() {
        return GameMode.MEDIUM;
    }
}
