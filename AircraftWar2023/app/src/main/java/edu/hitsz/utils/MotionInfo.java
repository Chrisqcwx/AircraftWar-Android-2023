package edu.hitsz.utils;

import java.util.Optional;

public class MotionInfo {


    private final int x;
    private final int y;
    private final int speedX;
    private final int speedY;

    public MotionInfo(int x, int y, int speedX, int speedY) {
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.speedY = speedY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSpeedX() {
        return speedX;
    }

    public int getSpeedY() {
        return speedY;
    }


}
