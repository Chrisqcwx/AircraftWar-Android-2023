package edu.hitsz.aircraft.utils;

import edu.hitsz.ImageManager;
//import edu.hitsz.application.Main;
import edu.hitsz.utils.params.swingParams.Windows;

public class Params {
    public static class Init {
        public static class ShootNum {
            public static final int HERO = 1;
            public static final int ELITE = 1;
            public static final int BOSS = 3;
        }

        public static class Power {
            public static final int HERO = 30;
            public static final int ELITE = 30;
            public static final int BOSS = 30;
        }

        public static class SpeedX {
            public static final int HERO = 0;
            public static final int MOB = 0;
            public static final int ELITE = 0;
            public static final int BOSS = 6;
        }

        public static class SpeedY {
            public static final int HERO = 0;
            public static final int MOB = 6;
            public static final int ELITE = 3;
            public static final int BOSS = 0;
        }

        public static class Hp {
            public static final int MOB = 30;
            public static final int ELITE = 50;
            public static final int BOSS = 1000;
            public static final int HERO = 1000;
        }

//        public static class LocationX {
//            public static final int HERO = Windows.WINDOW_WIDTH / 2;
//        }
//
//        public static class LocationY {
//            public static  int HERO = Windows.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight();
//        }
    }
}
