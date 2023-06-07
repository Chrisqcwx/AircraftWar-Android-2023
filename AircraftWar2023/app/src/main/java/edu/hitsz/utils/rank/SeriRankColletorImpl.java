package edu.hitsz.utils.rank;

import android.content.Context;
import android.content.ContextWrapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Chris
 */
public class SeriRankColletorImpl implements IRankCollectorDAO {
    private List<RankDataStruct> data = new LinkedList<RankDataStruct>();

    private String filename;
    private Context context;

    public SeriRankColletorImpl(Context context, String filename){
        this.context = context;
        this.filename = filename;
            try(ObjectInputStream ois = new ObjectInputStream(
                    context.openFileInput(this.filename))) {
                this.data = (List<RankDataStruct>) ois.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
//        }
    }

    @Override
    public int add(RankDataStruct rankData) {
        for(int i = 0;i < data.size(); i++ ) {
            if (data.get(i).getScore() < rankData.getScore()) {
                data.add(i, rankData);
                return i;
            }
        }
        data.add(rankData);
        return data.size() - 1;
    }

    @Override
    public void delete(int rank) {
        if (rank < 0 || rank >= data.size()) {
            return;
        }
        data.remove(rank);
    }

    @Override
    public void deleteItems(List<Integer> ranks) {
        if (ranks == null) {
            return;
        }

        synchronized (data) {
            List<RankDataStruct> removeData = ranks.stream().map(i -> data.get(i)).collect(Collectors.toList());
            removeData.forEach(r -> data.remove(r));
        }
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public void save(){
//        ObjectOutputStream oos;
        try(ObjectOutputStream oos = new ObjectOutputStream(context.openFileOutput(this.filename, Context.MODE_PRIVATE))) {
            oos.writeObject(data);

        }catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public final List<RankDataStruct> getAllData() {
        return data;
    }

    @Override
    public String toString() {
        List<String> ls = new LinkedList<>();
        for (int i = 0; i < data.size(); i++) {
            RankDataStruct d = data.get(i);
            ls.add((new StringBuffer())
                    .append("rank ")
                    .append(i + 1)
                    .append(": ")
                    .append(data.get(i).toString())
                    .toString());
        }
        return String.join("\r\n", ls);
    }

    @Override
    public int size() {
        return data.size();
    }
}
