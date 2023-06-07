package edu.hitsz.utils.rank;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.hitsz.adapters.HistoryRecord;


public class RankDataStruct implements Serializable{

    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
    private int score;
    private Date time;
    private String player;

    public String getPlayer() {
        return player;
    }

    public int getScore() {
        return score;
    }

    public Date getTime() {
        return time;
    }


    public RankDataStruct(String player, int score, Date time) {
        this.player = player;
        this.score = score;
        this.time = time;
    }

    public RankDataStruct(HistoryRecord rec) {
        this.player = rec.name;
        this.score = rec.score;
        try {
            this.time = format.parse(rec.time);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public String toString() {
        return String.join(",", player,
                String.valueOf(score), format.format(time));
    }
}