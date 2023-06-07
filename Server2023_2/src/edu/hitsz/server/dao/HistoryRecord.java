package edu.hitsz.server.dao;

import org.json.*;

/**
 * @author Chris
 */
public class HistoryRecord {
    public String name;
    public int score;
    public String time;
    public HistoryRecord(String name, int score, String time) {
        this.name = name;
        this.score = score;
        this.time = time;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HistoryRecord) {
            HistoryRecord record = (HistoryRecord) obj;
            return this.name.equals(record.name) && this.score == record.score && this.time.equals(record.time);
        }
        return false;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("NAME", this.name);
        json.put("SCORE", this.score);
        json.put("TIME", this.time);
        return json;
    }

    public static HistoryRecord fromJSON(JSONObject json) throws JSONException {
        if (json == null) {
            return null;
        }
        HistoryRecord res = new HistoryRecord(
                json.getString("NAME"),
                json.getInt("SCORE"),
                json.getString("TIME")
        );
        return res;
    }
}
