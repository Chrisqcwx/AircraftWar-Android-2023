package edu.hitsz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.aircraft.enemyaircraft.BossEnemy;
import edu.hitsz.aircraft.enemyaircraft.EliteEnemy;
import edu.hitsz.aircraft.enemyaircraft.MobEnemy;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.bullet.HeroFireBallBullet;
import edu.hitsz.prop.BloodProp;
import edu.hitsz.prop.BombProp;
import edu.hitsz.prop.FireBallProp;
import edu.hitsz.prop.FireProp;
import edu.hitsz.prop.RocketProp;

public class ImageManager {
    private static final String TAG = "ImageManager";
    /**
     * 类名-图片 映射，存储各基类的图片 <br>
     * 可使用 CLASSNAME_IMAGE_MAP.get( obj.getClass().getName() ) 获得 obj 所属基类对应的图片
     */
    private static final Map<String, Bitmap> CLASSNAME_IMAGE_MAP = new HashMap<>();

    public static Bitmap BACKGROUND1_IMAGE;
    public static Bitmap BACKGROUND2_IMAGE;
    public static Bitmap BACKGROUND3_IMAGE;
    public static Bitmap HERO_IMAGE;
    public static Bitmap HERO_HIT_IMAGE;
    public static Bitmap HERO_BULLET_IMAGE;
    public static Bitmap HERO_FIREBALL_BULLET_IMAGE;
    public static Bitmap ENEMY_BULLET_IMAGE;
    public static Bitmap MOB_ENEMY_IMAGE;
    public static Bitmap MOB_DIE_ENEMY_IMAGE;
    public static Bitmap ELITE1_ENEMY_IMAGE;
    public static Bitmap ELITE2_ENEMY_IMAGE;
    public static Bitmap ELITE3_ENEMY_IMAGE;
    public static Bitmap BOSS_ENEMY_IMAGE;
    public static Bitmap FIRE_SUPPLY_IMAGE;
    public static Bitmap HP_SUPPLY_IMAGE;
    public static Bitmap BOMB_SUPPLY_IMAGE;
    public static Bitmap FIREBALL_SUPPLY_IMAGE;
    public static Bitmap ROCKET_SUPPLY_IMAGE;


    public static Bitmap BACKGROUND1_bg;

    public static void initImage(Context context){

        ImageManager.BACKGROUND1_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg);
        ImageManager.HERO_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.hero37);
        ImageManager.HERO_HIT_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.hero37hit);
        ImageManager.BACKGROUND2_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg2);
        ImageManager.BACKGROUND3_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg3);
        ImageManager.MOB_ENEMY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.mob_w);
        ImageManager.MOB_DIE_ENEMY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.mobdie);
        ImageManager.ELITE1_ENEMY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.elite1);
        ImageManager.ELITE2_ENEMY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.elite2);
        ImageManager.ELITE3_ENEMY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.elite3);
        ImageManager.HERO_BULLET_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet_hero);
        ImageManager.HERO_FIREBALL_BULLET_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.fireball);
        ImageManager.ENEMY_BULLET_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet_enemy);
        ImageManager.FIRE_SUPPLY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.prop_bullet);
        ImageManager.HP_SUPPLY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.prop_blood);
        ImageManager.BOMB_SUPPLY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.prop_bomb);
        ImageManager.ROCKET_SUPPLY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.prop_rocket);
        ImageManager.FIREBALL_SUPPLY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.prop_fireball);
        ImageManager.BOSS_ENEMY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.boss1);


        CLASSNAME_IMAGE_MAP.put(HeroAircraft.class.getName(), HERO_IMAGE);
        CLASSNAME_IMAGE_MAP.put(MobEnemy.class.getName(), MOB_ENEMY_IMAGE);
//        CLASSNAME_IMAGE_MAP.put(EliteEnemy.class.getName(), ELITE_ENEMY_IMAGE);
        CLASSNAME_IMAGE_MAP.put(BossEnemy.class.getName(), BOSS_ENEMY_IMAGE);

        CLASSNAME_IMAGE_MAP.put(HeroBullet.class.getName(), HERO_BULLET_IMAGE);
        CLASSNAME_IMAGE_MAP.put(HeroFireBallBullet.class.getName(), HERO_FIREBALL_BULLET_IMAGE);
        CLASSNAME_IMAGE_MAP.put(EnemyBullet.class.getName(), ENEMY_BULLET_IMAGE);

        CLASSNAME_IMAGE_MAP.put(FireProp.class.getName(), FIRE_SUPPLY_IMAGE);
        CLASSNAME_IMAGE_MAP.put(BombProp.class.getName(), BOMB_SUPPLY_IMAGE);
        CLASSNAME_IMAGE_MAP.put(BloodProp.class.getName(), HP_SUPPLY_IMAGE);
        CLASSNAME_IMAGE_MAP.put(RocketProp.class.getName(), ROCKET_SUPPLY_IMAGE);
        CLASSNAME_IMAGE_MAP.put(FireBallProp.class.getName(), FIREBALL_SUPPLY_IMAGE);
    }

    public static Bitmap get(String className){
        Bitmap res = null;
        if (EliteEnemy.class.getName().equals(className)) {
            double r = new Random().nextDouble();
            if (r<0.33) {
                res = ELITE1_ENEMY_IMAGE;
            }else if (r<0.67) {
                res = ELITE2_ENEMY_IMAGE;
            }else {
                res = ELITE3_ENEMY_IMAGE;
            }
        }else {
            res = CLASSNAME_IMAGE_MAP.get(className);
        }

        if (res == null) {
            Log.e(TAG, "Class name: "+className+" not found");
        }
        return res;
    }

    public static Bitmap get(Object obj){
        if (obj == null){
            Log.e(TAG, "game is null");
            return null;
        }
        return get(obj.getClass().getName());
    }
}
