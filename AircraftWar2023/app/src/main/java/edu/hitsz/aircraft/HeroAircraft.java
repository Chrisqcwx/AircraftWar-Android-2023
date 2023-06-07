package edu.hitsz.aircraft;

import android.graphics.Bitmap;
import android.util.Log;

import edu.hitsz.ImageManager;
import edu.hitsz.activity.SelectGameModeActivity;
import edu.hitsz.aircraft.utils.CircleBulletGenerator;
import edu.hitsz.aircraft.utils.DirectBulletGenerator;
//import edu.hitsz.application.ImageManager;
import edu.hitsz.bullet.HeroFireBallBullet;
import edu.hitsz.utils.MotionInfo;
import edu.hitsz.aircraft.utils.Params;
import edu.hitsz.aircraft.utils.BaseBulletGenerator;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.utils.params.Direction;

import java.util.LinkedList;
import java.util.List;

/**
 * 英雄飞机，游戏玩家操控
 * @author hitsz
 */
public class HeroAircraft extends AbstractAircraft implements IHeroAircraftForProp {

    private static final String TAG = "HeroAircraft";
    /**
     * 子弹一次发射数量
     */
//    private int shootNum = Params.Init.ShootNum.HERO;

    /**
     * 子弹伤害
     */
    private int power = Params.Init.Power.HERO;

    /**
     * 子弹射击方向 (向上发射：1，向下发射：-1)
     */
    private int direction = Direction.UP;

//    BulletGenerator bulletGenerator = new DirectBulletGenerator(Params.Init.ShootNum.HERO);

    /**
     * @param locationX 英雄机位置x坐标
     * @param locationY 英雄机位置y坐标
     * @param speedX 英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param speedY 英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param hp    初始生命值
     */
    private HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp, BaseBulletGenerator bulletGenerator) {
        super(locationX, locationY, speedX, speedY, hp, bulletGenerator);
    }

    private static volatile HeroAircraft heroAircraft = null;

//    public void setUp(int initHp) {
//        this.hp = initHp;
//
//    }
    public static HeroAircraft getInstance() {
        if (heroAircraft == null || heroAircraft.notValid()) {
            synchronized (HeroAircraft.class) {
                if (heroAircraft == null || heroAircraft.notValid()) {

                    heroAircraft = new HeroAircraft(
                        SelectGameModeActivity.screenWidth / 2,
                            SelectGameModeActivity.screenHeight - ImageManager.HERO_IMAGE.getHeight(),
                        Params.Init.SpeedX.HERO,
                        Params.Init.SpeedY.HERO,
                        Params.Init.Hp.HERO,
                        new DirectBulletGenerator(Params.Init.ShootNum.HERO)
                    );
                }
            }
        }
        return heroAircraft;
    }

    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }

    private List<BaseBullet> addBullets = new LinkedList<>();
    @Override
    public void addRocketBullet(int x, int y) {
        int speed = direction * 10;
//        var generator = new RoundBulletGenerator(3);
        CircleBulletGenerator generator = new CircleBulletGenerator(30);
//        addBullets.addAll(generator.createBullets(x, y, speed, this::getBullet));
        addBullets = generator.createBullets(x, y, speed, this::getBullet);
    }

    private boolean isFireBallBullet = false;

    @Override
    public void setIsFireBallBullet(boolean value) {
        this.isFireBallBullet = value;
    }


    private BaseBullet getBullet(MotionInfo motionInfo) {
        if (isFireBallBullet) {
            return new HeroFireBallBullet(motionInfo, power + 5);
        }else {
            return new HeroBullet(motionInfo, power);
        }
    }

    @Override
    /**
     * 通过射击产生子弹
     * @return 射击出的子弹List
     */
    public List<BaseBullet> shoot() {
        int x = this.getLocationX();
        int y = this.getLocationY() + direction*2;
        int speed = this.getSpeedY() + direction*10;
        List<BaseBullet> res = new LinkedList<>();
        res.addAll(bulletGenerator.createBullets(x, y, speed, this::getBullet));
//        System.out.println("ddd");
        if (addBullets != null) {
//            System.out.println("ddd");
            res.addAll(addBullets);
//            System.out.println("eee");
            addBullets = new LinkedList<>();
//            System.out.println("fff");
        }

        return res;
    }

    @Override
    public void increaseHp(int hp) {
        this.hp = Math.min(this.hp + hp, this.maxHp);
    }

    private static int decreaseCnt = 0;
    private static final Integer lock = 0;

    @Override
    public Bitmap getImage() {
        if (decreaseCnt == 0) {
            return ImageManager.HERO_IMAGE;
        }else {
            Log.i(TAG, "set hero hit");
            return ImageManager.HERO_HIT_IMAGE;
        }
    }
    @Override
    public void decreaseHp(int decrease) {
        super.decreaseHp(decrease);

        new Thread( () -> {
            synchronized (lock) {
                decreaseCnt++;
            }
//            this.image = ImageManager.HERO_HIT_IMAGE;

            Log.i(TAG, "change to angry: "+decreaseCnt);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.i(TAG, "interrupt");
                e.printStackTrace();
            }
            Log.i(TAG, "change to smile: "+decreaseCnt);
            synchronized (lock) {
                if (decreaseCnt == 1) {
//                    this.image = ImageManager.HERO_IMAGE;
                }
                decreaseCnt--;
            }
        }).start();
    }
}
