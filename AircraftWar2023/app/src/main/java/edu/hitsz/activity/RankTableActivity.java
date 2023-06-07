package edu.hitsz.activity;

import static edu.hitsz.utils.LogUtils.logException;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import edu.hitsz.R;
import edu.hitsz.adapters.HistoryRecord;
import edu.hitsz.adapters.RankItemAdapter;
import edu.hitsz.adapters.RankItemData;
import edu.hitsz.utils.params.GameMode;
import edu.hitsz.utils.rank.IRankCollectorDAO;
import edu.hitsz.utils.rank.RankCollectors;
import edu.hitsz.utils.rank.RankDataStruct;

public class RankTableActivity extends AppCompatActivity {

    private static final String TAG = "RankTableActivity";
    private int gameType;

    private boolean isOnline;

    private boolean offlineThisData;

    private void setLabel() {
        Intent intent;
        if ((intent = getIntent()) != null) {
            int gameType = intent.getIntExtra("gameType", GameMode.EASY);
            TextView textView = findViewById(R.id.rank_mode);
            switch (gameType) {
                case GameMode.EASY:
                    textView.setText(R.string.EasyGame);
                    break;
                case GameMode.MEDIUM:
                    textView.setText(R.string.MediumGame);
                    break;
                case GameMode.HARD:
                    textView.setText(R.string.HardGame);
                    break;
                default:
                    break;
            }
        }
    }

//    private int selectItemPos = -1;
//    private RankDataStruct selectItem;
//    private View selectView;
    private IRankCollectorDAO dao;

    private static class SelectItemStruct {
        public RankItemData item;
        public View view;
    }
    private final Map<Integer, SelectItemStruct> selectItems = new HashMap<>();

    private void setShape(View itemView, int colorId) {
//         shape = itemView.findViewById(R.id.rank_item_color);
        GradientDrawable shape= (GradientDrawable) itemView
                .findViewById(R.id.rank_item_layout)
                .getBackground();
        shape.setColor(getColor(colorId));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_table);
        setLabel();

        ListView mListView = findViewById(R.id.rank_listview);
//        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        Button deleteButton = findViewById(R.id.rank_delete);
//        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        Button returnButton = findViewById(R.id.rank_return);

        gameType = getIntent().getIntExtra("gameType", -1);
        if (gameType == -1) {
            throw new RuntimeException("invalid gameType");
        }

        List<RankItemData> data = new LinkedList<>();

        isOnline = getIntent().getBooleanExtra("isOnline",false);

        if (isOnline) {
            deleteButton.setVisibility(View.INVISIBLE);
            try {
                JSONObject dataJSON = new JSONObject(getIntent().getStringExtra("data"));
                data = getOnlineDataFromJSON(dataJSON);
            } catch (JSONException e) {
                logException(TAG, e);
//                Log.e(TAG, e.getMessage());
//                Log.e(TAG, String.valueOf(Arrays.stream(e.getStackTrace()).map(Objects::toString).reduce((a, b)->(a + "\n"+ b))));
            }
        }else {
            int rank = getIntent().getIntExtra("thisRank", -1);
            dao = RankCollectors.getRankCollector(gameType);
            data = getDataFromDAO(dao, rank);

        }


//        for (int i=0;i<5;i++)
//        {
//            data.add(new RankDataStruct("a", 12, new Date()));
//            data.add(new RankDataStruct("bb", 1275, new Date()));
//            data.add(new RankDataStruct("ccc", 78, new Date()));
//        }

        RankItemAdapter adapter = new RankItemAdapter(this, R.layout.rank_item, data);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener((parent, view, position, id) -> {
            if (isOnline) {
                return;
            }
            Log.v(TAG, "HIT position: "+position);

            SelectItemStruct selectItem = selectItems.getOrDefault(position, null);

            if (selectItem == null) {
                SelectItemStruct item = new SelectItemStruct();
                item.item = (RankItemData) parent.getItemAtPosition(position);
                item.view = view;
//                item.view.setBackgroundColor(getColor(R.color.medium_blue));
//                setShape(item.view, R.color.medium_blue);
                adapter.addSelectedItem(position);
                selectItems.put(position, item);
            }else {
//                selectItem.view.setBackgroundColor(getColor(R.color.light_blue))
//                setShape(selectItem.view, R.color.light_blue);
                adapter.removeSelectedItem(position);
                selectItems.remove(position);
            }
        });

        deleteButton.setOnClickListener(view -> {
            if (isOnline) {
                return ;
            }
            if (selectItems.size() == 0) {
                Toast.makeText(this, "未选中", Toast.LENGTH_SHORT).show();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(RankTableActivity.this);

            builder.setTitle("是否要删除选中的所有历史记录");

            builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    selectItems.forEach((pos, item) -> {
//                item.view.setBackgroundColor(getColor(R.color.light_blue));
                        setShape(item.view, R.color.light_blue);
                    });

                    dao.deleteItems(selectItems.values().stream().map(item ->
                            item.item.rank - 1
                    ).collect(Collectors.toList()));
                    dao.save();

                    adapter.clear();
                    adapter.addAll(getDataFromDAO(dao, -1));

                    selectItems.clear();
                }
            });
            builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.show();

        });

        returnButton.setOnClickListener(view -> finish());
    }

    private List<RankItemData> getOnlineDataFromJSON(JSONObject dataJSON) throws JSONException {
        JSONArray histDataJSON = dataJSON.getJSONArray("histData");
        int histDataLen = dataJSON.getInt("histDataLength");
        int thisRank = dataJSON.getInt("thisRank");
        int otherRank = dataJSON.getInt("otherRank");
        JSONObject thisDataJSON = dataJSON.getJSONObject("thisData");
        JSONObject otherDataJSON = dataJSON.getJSONObject("otherData");

        TextView winLossText = this.findViewById(R.id.rank_table_text);
        HistoryRecord thisRecord = HistoryRecord.fromJSON(thisDataJSON);
        HistoryRecord otherRecord = HistoryRecord.fromJSON(otherDataJSON);
        boolean isWin = thisRecord.score >= otherRecord.score;
        if (isWin) {
            winLossText.setText("VICTORY");
        }else {
            winLossText.setText("DEFEAT");
        }

        List<RankItemData> data = new LinkedList<>();
        for (int i=0;i<histDataLen;i++){
            data.add(new RankItemData(i+1,
                    new RankDataStruct(
                            HistoryRecord.fromJSON(histDataJSON.getJSONObject(i))
                    ),
                    false
            ));
        }
        if(isWin){
            if (thisRank < data.size()) {
                data.get(thisRank).isHighLight = true;
            } else {
                data.add(new RankItemData(thisRank + 1,
                        new RankDataStruct(
//                            HistoryRecord.fromJSON(thisDataJSON)
                                thisRecord
                        ),
                        true
                ));
            }
            if (otherRank < data.size()) {
                data.get(otherRank).isHighLight = true;
            } else {
                data.add(new RankItemData(otherRank + 1,
                        new RankDataStruct(
//                            HistoryRecord.fromJSON(otherDataJSON)
                                otherRecord
                        ),
                        true
                ));
            }
        }
        else{
            if (otherRank < data.size()) {
                data.get(otherRank).isHighLight = true;
            } else {
                data.add(new RankItemData(otherRank + 1,
                        new RankDataStruct(
//                            HistoryRecord.fromJSON(otherDataJSON)
                                otherRecord
                        ),
                        true
                ));
            }

            if (thisRank < data.size()) {
                data.get(thisRank).isHighLight = true;
            } else {
                data.add(new RankItemData(thisRank + 1,
                        new RankDataStruct(
//                            HistoryRecord.fromJSON(thisDataJSON)
                                thisRecord
                        ),
                        true
                ));
            }
        }
//        if (thisRank < data.size() && otherRank < data.size() && !isWin) {
//            RankItemData tmp = data.get(data.size()-1);
//            data.set(data.size()-1, data.get(data.size()-2));
//            data.set(data.size()-2, tmp);
//        }

        return data;
    }

    private List<RankItemData> getDataFromDAO(IRankCollectorDAO dao, int rank) {
        List<RankDataStruct> rawData = dao.getAllData();
        List<RankItemData> data = new LinkedList<>();
        IntStream.range(0, rawData.size()).forEach(
                i -> data.add(new RankItemData(i+1, rawData.get(i), i == rank) )
        );

        return data;

    }
}