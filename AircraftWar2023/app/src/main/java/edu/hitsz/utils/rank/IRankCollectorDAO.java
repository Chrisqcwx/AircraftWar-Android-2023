package edu.hitsz.utils.rank;

import java.io.IOException;
import java.util.List;

public interface IRankCollectorDAO {
    int add(RankDataStruct rankData);
    void delete(int rank);
    void deleteItems(List<Integer> ranks);
    @Override
    String toString();
    void clear();
    void save();
    List<RankDataStruct> getAllData();
    int size();
}
