package edu.hitsz.utils;

public class Counter {
    private int maxCount;
    private int count;
    public Counter(int maxNum) {
        this.maxCount = maxNum;
        this.count = 0;
    }

    public boolean inc() {
        this.count += 1;
        if (this.count > this.maxCount) {
            this.count = 0;
            return true;
        }
        else {
            return false;
        }
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }
}
