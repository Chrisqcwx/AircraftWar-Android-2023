package edu.hitsz.adapters;

import java.util.Date;

import edu.hitsz.utils.rank.RankDataStruct;

public class RankItemData {
    public int rank;
    public int score;
    public String time;
    public String player;
    public boolean isHighLight;

    public RankItemData(int rank, RankDataStruct baseData, boolean isHighLight) {
        this.rank = rank;
        this.score = baseData.getScore();
        this.time = RankDataStruct.format.format(baseData.getTime());
        this.player = baseData.getPlayer();
        this.isHighLight = isHighLight;
    }
}
