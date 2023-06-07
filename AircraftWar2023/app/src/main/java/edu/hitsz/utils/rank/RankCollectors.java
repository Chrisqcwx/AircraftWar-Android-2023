package edu.hitsz.utils.rank;

import android.content.Context;

import edu.hitsz.R;
import edu.hitsz.utils.params.GameMode;
import edu.hitsz.utils.params.PathParams;

import java.util.HashMap;

public class RankCollectors {
    public static SeriRankColletorImpl simpleRankCollector;// = new SeriRankColletorImpl((PathParams.SERI_SIMPLE_DATA));
    public static SeriRankColletorImpl commonRankCollector;// = new SeriRankColletorImpl((PathParams.SERI_COMMON_DATA));
    public static SeriRankColletorImpl hardRankCollector;// = new SeriRankColletorImpl((PathParams.SERI_HARD_DATA));

    private static final HashMap<Integer, IRankCollectorDAO> map = new HashMap<Integer, IRankCollectorDAO>();
    private static final HashMap<Integer, String> strmap = new HashMap<Integer, String>();

    static  {
//        map.put(GameMode.SIMPLE, simpleRankCollector);
//        map.put(GameMode.COMMON, commonRankCollector);
//        map.put(GameMode.HARD, hardRankCollector);
        strmap.put(GameMode.EASY, PathParams.SERI_SIMPLE_DATA);
        strmap.put(GameMode.MEDIUM, PathParams.SERI_COMMON_DATA);
        strmap.put(GameMode.HARD, PathParams.SERI_HARD_DATA);
    }

    public static void init(Context context) {
        map.put(GameMode.EASY,
                new SeriRankColletorImpl(context, strmap.get(GameMode.EASY)));
        map.put(GameMode.MEDIUM,
                new SeriRankColletorImpl(context, strmap.get(GameMode.MEDIUM)));
        map.put(GameMode.HARD,
                new SeriRankColletorImpl(context, strmap.get(GameMode.HARD)));
    }

    public static IRankCollectorDAO getRankCollector(int gameId) {
        SeriRankColletorImpl res = (SeriRankColletorImpl) map.get(gameId);
        assert res != null;
        return res;
    }
}
