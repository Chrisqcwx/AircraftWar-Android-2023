package edu.hitsz.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.hitsz.ImageManager;
import edu.hitsz.activity.MainActivity;
import edu.hitsz.activity.SelectGameModeActivity;
import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.aircraft.ICreateReward;
import edu.hitsz.aircraft.enemyaircraft.AbstractEnemyAircraft;
import edu.hitsz.aircraft.enemyaircraft.BossEnemy;
import edu.hitsz.aircraft.enemyaircraft.factory.AbstractEnemyFactory;
import edu.hitsz.aircraft.enemyaircraft.factory.RandomEnemyFactoryGenerator;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroFireBallBullet;
import edu.hitsz.prop.BaseProp;
import edu.hitsz.utils.Counter;

/**
 * 游戏逻辑抽象基类，遵循模板模式，action() 为模板方法
 * 包括：游戏主面板绘制逻辑，游戏执行逻辑。
 * 子类需实现抽象方法，实现相应逻辑
 * @author hitsz
 */
public abstract class BaseGame extends SurfaceView implements SurfaceHolder.Callback, Runnable{

    public static final String TAG = "BaseGame";
    boolean mbLoop = false; //控制绘画线程的标志位
    private SurfaceHolder mSurfaceHolder;
    private Canvas canvas;  //绘图的画布
    private Paint mPaint;
    private Handler handler;

    //点击屏幕位置
    float clickX = 0, clickY=0;

    private int backGroundTop = 0;

    /**
     * 背景图片缓存，可随难度改变
     */
    protected Bitmap backGround;



    /**
     * 时间间隔(ms)，控制刷新频率
     */
    private int timeInterval = 16;

    private final HeroAircraft heroAircraft;

    protected final ConcurrentLinkedQueue<AbstractEnemyAircraft> enemyAircrafts;

    private final ConcurrentLinkedQueue<BaseBullet> heroBullets;
    private final ConcurrentLinkedQueue<BaseBullet> enemyBullets;
    private final ConcurrentLinkedQueue<BaseProp> props;

    protected int enemyMaxNumber = 2;

    private boolean gameOverFlag = false;
    private int score = 0;
    private int otherPlayerScore = 0;
    private int otherPlayerHp = 1000;
    private int time = 0;
    private String otherPlayerName;
    private String thisName;


    // ADD
    protected abstract int getTimeInterval();
    protected abstract int getEnemyMaxNumber();

    /**
     * 有无BOSS
     */
    private boolean hasBoss = false;
    private int bossCreateNum = 0;
    /**
     * 随机选择生成器
     */
    private RandomEnemyFactoryGenerator enemyFactorySeletor;
    protected abstract List<Float> getRandomWeights();

    /**
     * 周期（ms)
     * 控制英雄机射击周期，默认值设为简单模式
     */
    private int cycleDuration = 600;
    private int cycleTime = 0;

    private Counter enemyShootCounter;
    private Counter heroShootCounter;
    private Counter createEnemyCounter;

    protected abstract int getEnemyShootCycleNum();
    protected abstract int getHeroShootCycleNum();
    protected abstract int getCreateEnemyCycleNum();

    protected abstract void heroShootUpdate();
    protected abstract void enemyShootUpdate();
    protected abstract void createEnemyUpdate();

    private boolean isOnline = false;

    public BaseGame(Context context, Handler handler, boolean isOnline){
        super(context);
        this.isOnline = isOnline;
        this.handler = handler;
        mbLoop = true;
        mPaint = new Paint();  //设置画笔
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);
        this.setFocusable(true);
        ImageManager.initImage(context);

        // 初始化英雄机
        heroAircraft = HeroAircraft.getInstance();
//        heroAircraft.setHp(1000);

        enemyAircrafts = new ConcurrentLinkedQueue<>();
        heroBullets = new ConcurrentLinkedQueue<>();
        enemyBullets = new ConcurrentLinkedQueue<>();
        props = new ConcurrentLinkedQueue<>();

        this.createEnemyCounter = new Counter(getCreateEnemyCycleNum());
        this.heroShootCounter = new Counter(getHeroShootCycleNum());
        this.enemyShootCounter = new Counter(getEnemyShootCycleNum());
        timeInterval = getTimeInterval();


        heroController();
    }
    /**
     * 游戏启动入口，执行游戏逻辑
     */
    public void action() {



        //new Thread(new Runnable() {
        Runnable task = () -> {

            time += timeInterval;
            // 周期性执行（控制频率）
            if (createEnemyCounter.inc()) {
//                System.out.println(time);
                createEnemyUpdate();
                createEnemyCounter.setMaxCount(getCreateEnemyCycleNum());
                // 设置随机生成器
                this.enemyFactorySeletor = new RandomEnemyFactoryGenerator(getRandomWeights());
                // 设置音乐
//                musicController.setStage(this.hasBoss);
                // 新敌机产生
                createEnemy();
            }

            if (heroShootCounter.inc()) {
                heroShootUpdate();
                heroShootCounter.setMaxCount(getHeroShootCycleNum());
                heroShootAction();
            }

            if (enemyShootCounter.inc()) {
                enemyShootUpdate();
                enemyShootCounter.setMaxCount(getCreateEnemyCycleNum());
                enemyShootAction();
            }

            // 子弹移动
            bulletsMoveAction();

            // Prop移动
            propsMoveAction();

            // 飞机移动
            aircraftsMoveAction();

            // 撞击检测
            crashCheckAction();

            // 摧毁敌机奖励
            enemyVanishReward();

            // 后处理
            postProcessAction();

            try {
                Thread.sleep(timeInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //}
        };
//        new Thread(task).start();
//        task.run();
        MainActivity.threadPool.execute(task);
    }

    public void heroController(){
        setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                clickX = motionEvent.getX();
                clickY = motionEvent.getY();
                heroAircraft.setLocation(clickX, clickY);

                if ( clickX<0 || clickX> SelectGameModeActivity.screenWidth || clickY<0 || clickY> SelectGameModeActivity.screenHeight){
                    // 防止超出边界
                    return false;
                }
                return true;
            }
        });
    }

    private final int createBossScore = 1600;
    private int nextBossCreateScore=1500;
    protected abstract boolean canCreateBoss();
    private boolean isCreateBoss() {

        if (canCreateBoss() && !hasBoss && this.score > nextBossCreateScore){
            hasBoss = true;
            sendMessage(GameMessage.what_setMusic, GameMessage.object_bossBgm);
            return true;
        }
        return false;
    }

    protected abstract int getIncreaseBossHp();
    protected abstract int getIncreaseEnemyHp();

    private void createEnemy() {
        if (enemyAircrafts.size() < getEnemyMaxNumber()) {

            boolean createBoss = isCreateBoss();
            AbstractEnemyFactory enemyFactory = enemyFactorySeletor.nextEnemyFactory(createBoss);
            if(enemyFactory == null) {
                return;
            }
            if (createBoss) {
                enemyFactory.increaseHp(getIncreaseBossHp()*bossCreateNum);
                bossCreateNum += 1;
            }else {
                int increaseHp = getIncreaseEnemyHp();

                enemyFactory.increaseHp(getIncreaseEnemyHp());
            }

            AbstractEnemyAircraft enemy = enemyFactory.createEnemy();
            if (enemy != null) {
//                System.out.println("*****"+enemy.getHp());
//                synchronized (enemyAircrafts) {
                    enemyAircrafts.add(enemy);
//                }

            }
        }
    }


    private void enemyShootAction() {
        // 敌机射击
//        synchronized (enemyBullets){
//            synchronized (enemyAircrafts) {
                enemyAircrafts.forEach(enemyAircraft->{
                    enemyBullets.addAll(enemyAircraft.shoot());
                });
//            }
//        }

    }

    private void heroShootAction() {
//        synchronized (heroBullets) {
            heroBullets.addAll(heroAircraft.shoot());
//        }

    }

    private void bulletsMoveAction() {
//        synchronized (heroBullets){
            heroBullets.forEach(BaseBullet::forward);
//        }
//        synchronized (enemyAircrafts){
            enemyBullets.forEach(BaseBullet::forward);
//        }
    }

    private void propsMoveAction() {
        props.forEach(BaseProp::forward);
    }


    private void aircraftsMoveAction() {
        enemyAircrafts.forEach(AbstractAircraft::forward);
    }


    /**
     * 碰撞检测：
     * 碰撞检测：
     * 1. 敌机攻击英雄
     * 2. 英雄攻击/撞击敌机
     * 3. 英雄获得补给
     */
    private void crashCheckAction() {
        //  敌机子弹攻击英雄
//        synchronized (enemyBullets) {
            enemyBullets.stream()
                    .filter(bullet -> this.crashCheck(this.heroAircraft, bullet))
                    .peek(bullet -> {
                        this.heroAircraft.decreaseHp(bullet.getPower());
                        bullet.vanish();
//                    musicController.bulletHit();
                    }).findAny().ifPresent(bullet -> sendMessage(GameMessage.what_hp, this.heroAircraft.getHp()));
//        }



        // 英雄子弹攻击敌机
//        synchronized (enemyAircrafts){
//            synchronized (heroBullets){
                enemyAircrafts.forEach(enemyAircraft ->
                                heroBullets.stream()
                                        .filter(bullet -> crashCheck(enemyAircraft, bullet))
                                        .forEach(bullet -> {
                                            enemyAircraft.decreaseHp(bullet.getPower());
                                            if (!(bullet instanceof HeroFireBallBullet)){
                                                bullet.vanish();
                                            }


                                            sendMessage(GameMessage.what_setMusic, GameMessage.object_bulletHit);
//                            musicController.bulletHit();
                                        })
                );
//            }
//        }


        // 英雄机 与 敌机 相撞，均损毁

//        synchronized (enemyAircrafts){
            enemyAircrafts.stream()
                    .filter(enemyAircraft -> (crashCheck(enemyAircraft, heroAircraft) ||
                            crashCheck(heroAircraft, enemyAircraft)))
                    .forEach(enemyAircraft -> {
                        enemyAircraft.vanish();
                        heroAircraft.decreaseHp(Integer.MAX_VALUE);
                    });
//        }



        // 我方获得道具，道具生效
//        synchronized (props){
            this.props.stream()
                    .filter(prop -> crashCheck(this.heroAircraft, prop))
                    .forEach(prop -> {
                        prop.effect(this.heroAircraft, this.enemyAircrafts, this.enemyBullets);
//                        prop.vanish();

                        sendMessage(GameMessage.what_setMusic, prop.getSoundName());
//                    musicController.getSupply(prop);
                    });
        }
//    }

    private boolean crashCheck(AbstractFlyingObject src, AbstractFlyingObject dst) {
        return !src.notValid() && !dst.notValid() && src.crash(dst);
    }

    private void enemyVanishReward() {
        // 摧毁敌机，掉落道具
//        synchronized (enemyAircrafts){
            List<AbstractEnemyAircraft> destroyEnemy = enemyAircrafts.stream().filter(AbstractEnemyAircraft::notValid)
                    .peek(this::reward).collect(Collectors.toList());
            if (!destroyEnemy.isEmpty()) {
                sendMessage(GameMessage.what_score, score);
            }
            // 判断摧毁Boss
            destroyEnemy
                    .stream().filter(enemy -> enemy instanceof BossEnemy)
                    .forEach(boss -> {
                        hasBoss = false;
                        nextBossCreateScore = this.score + createBossScore;
//                    Log.e(TAG, "set boss bgm -> bgm");
                        sendMessage(GameMessage.what_setMusic, GameMessage.object_bgm);
                    });
//        }
    }

    private void reward(ICreateReward enemy) {
        // 获得分数，产生道具补给
        this.score += enemy.getScore();
        List<BaseProp> props = enemy.getProps();
        props.stream()
                .filter(Objects::nonNull)
                .forEach(this.props::add);
    }

    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * 3. 检查英雄机生存
     * <p>
     * 无效的原因可能是撞击或者飞出边界
     */
    private void postProcessAction() {
//        synchronized (enemyBullets) {
            enemyBullets.removeIf(AbstractFlyingObject::notValid);
//        }
//        synchronized (heroBullets) {
            heroBullets.removeIf(AbstractFlyingObject::notValid);
//        }
//        synchronized (enemyAircrafts) {
            enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
//        }
//        synchronized (props) {
            props.removeIf(AbstractFlyingObject::notValid);
//        }


        if (heroAircraft.notValid()) {
            gameOverFlag = true;
            mbLoop = false;
            Log.i(TAG, "heroAircraft is not Valid");
//            rankAddSave();
        }

    }

    public void draw() {
        canvas = mSurfaceHolder.lockCanvas();
        if(mSurfaceHolder == null || canvas == null){
            return;
        }

        //绘制背景，图片滚动
        canvas.drawBitmap(backGround,0,this.backGroundTop-backGround.getHeight(),mPaint);
        canvas.drawBitmap(backGround,0,this.backGroundTop,mPaint);
        backGroundTop +=1;
        if (backGroundTop == SelectGameModeActivity.screenHeight)
            this.backGroundTop = 0;

        //先绘制子弹，后绘制飞机
        paintImageWithPositionRevised(enemyBullets); //敌机子弹


        paintImageWithPositionRevised(heroBullets);  //英雄机子弹

        paintImageWithPositionRevised(props);

        paintImageWithPositionRevised(enemyAircrafts);//敌机


        canvas.drawBitmap(heroAircraft.getImage(),
                heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY()- ImageManager.HERO_IMAGE.getHeight() / 2,
                mPaint);

        //画生命值
        paintScoreAndLife();

        mSurfaceHolder.unlockCanvasAndPost(canvas);

    }

    private void paintImageWithPositionRevised(ConcurrentLinkedQueue<? extends AbstractFlyingObject> objects) {
        if (objects.size() == 0) {
            return;
        }

//        synchronized (objects)
        {
            for (AbstractFlyingObject object : objects) {
                Bitmap image = object.getImage();
                assert image != null : object.getClass().getName() + " has no image! ";
                canvas.drawBitmap(image, object.getLocationX() - image.getWidth() / 2,
                        object.getLocationY() - image.getHeight() / 2, mPaint);
            }

        }

    }

    private void paintScoreAndLife() {
        int x = 10;
        int y = 40;

        mPaint.setColor(Color.RED);
        mPaint.setTextSize(50);

        if (thisName == null) {
            canvas.drawText("YOU", x, y, mPaint);
        }else {
            canvas.drawText("YOU: "+thisName, x, y, mPaint);
        }
        y = y + 60;
        canvas.drawText("SCORE:" + this.score, x, y, mPaint);
        y = y + 60;
        canvas.drawText("LIFE:" + this.heroAircraft.getHp(), x, y, mPaint);

        if (isOnline) {
            x = SelectGameModeActivity.screenWidth / 2;
            y = 40;

            canvas.drawText("OTHER: "+otherPlayerName, x, y, mPaint);
            y = y + 60;
            canvas.drawText("SCORE:" + this.otherPlayerScore, x, y, mPaint);
            y = y + 60;
            canvas.drawText("LIFE:" + this.otherPlayerHp, x, y, mPaint);
        }
    }
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        new Thread(this).start();
        Log.i(TAG, "start surface view thread");
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        SelectGameModeActivity.screenWidth = i1;
        SelectGameModeActivity.screenHeight = i2;
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        mbLoop = false;
    }

    @Override
    public void run() {

        sendMessage(GameMessage.what_setMusic, GameMessage.object_bgm);
        while (mbLoop){   //游戏结束停止绘制
            synchronized (mSurfaceHolder){
                action();
                draw();
            }
        }
        sendMessage(GameMessage.what_setMusic, GameMessage.object_gameOver);

        sendMessage(GameMessage.what_gameover, score);

    }

    private void sendMessage(int what, Object obj) {
        Message message = Message.obtain();
        message.what = what ;
        message.obj = obj;
        handler.sendMessage(message);
    }

    protected abstract int getGameId();

    public void setOtherPlayerHp(int otherPlayerHp) {
        this.otherPlayerHp = otherPlayerHp;
    }

    public void setOtherPlayerScore(int otherPlayerScore) {
        this.otherPlayerScore = otherPlayerScore;
    }

    public void setName(String thisName, String otherPlayerName){
        this.thisName = thisName;
        this.otherPlayerName = otherPlayerName;
    }
}
