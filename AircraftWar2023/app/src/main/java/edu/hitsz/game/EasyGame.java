package edu.hitsz.game;

import android.content.Context;
import android.os.Handler;

import java.util.Arrays;
import java.util.List;

import edu.hitsz.ImageManager;
import edu.hitsz.R;
import edu.hitsz.utils.params.GameMode;

public class EasyGame extends BaseGame{

    public EasyGame(Context context, Handler handler, boolean isOnline) {
        super(context, handler, isOnline);
        this.backGround = ImageManager.BACKGROUND1_IMAGE;
    }

    @Override
    protected int getEnemyShootCycleNum() {
        return 45;
    }

    @Override
    protected int getHeroShootCycleNum() {
        return 45;
    }

    @Override
    protected int getCreateEnemyCycleNum() {
        return 45;
    }

    @Override
    protected void heroShootUpdate() {

    }

    @Override
    protected void enemyShootUpdate() {

    }

    @Override
    protected void createEnemyUpdate() {

    }

    private List<Float> enemyWeights = Arrays.asList(80f, 20f);


    @Override
    protected boolean canCreateBoss() {
        return false;
    }

    @Override
    protected int getIncreaseBossHp() {
        return 0;
    }

    @Override
    protected int getIncreaseEnemyHp() {
        return 0;
    }

    @Override
    protected int getGameId() {
        return GameMode.EASY;
    }

    @Override
    protected int getTimeInterval() {
//        return 45;
        return 18;
    }

    @Override
    protected int getEnemyMaxNumber() {
        return 4;
    }


    @Override
    protected List<Float> getRandomWeights(){
        return enemyWeights;
    }

}
