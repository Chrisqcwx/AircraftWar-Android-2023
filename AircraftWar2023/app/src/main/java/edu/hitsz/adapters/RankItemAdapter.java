package edu.hitsz.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.hitsz.R;

public class RankItemAdapter extends ArrayAdapter<RankItemData> {

    private static final String TAG = "RankItemAdapter";

    private final int resourceId;
    private final Set<Integer> selectedItems = new HashSet<>();
    public RankItemAdapter(@NonNull Context context, int resource, @NonNull List<RankItemData> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    public void addSelectedItem(int i) {
        selectedItems.add(i);
        notifyDataSetChanged();
    }

    public void removeSelectedItem(int i) {
        selectedItems.remove(i);
        notifyDataSetChanged();
    }

    @Override
    public void clear() {
        super.clear();
        selectedItems.clear();
    }


    private static class ViewHolder {
        public TextView rank, player, score, date;
        public ViewHolder(TextView rank, TextView player, TextView score, TextView date){
            this.rank = rank;
            this.player = player;
            this.score = score;
            this.date = date;
        }
    }

    @SuppressLint({"ViewHolder", "ResourceAsColor"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;

        if (convertView == null) {
//        if (true){
            view= LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            TextView rankText = view.findViewById(R.id.rank_rank);
            TextView dateText = view.findViewById(R.id.rank_time);
            TextView scoreText = view.findViewById(R.id.rank_score);
            TextView playerText = view.findViewById(R.id.rank_player);
            viewHolder = new ViewHolder(rankText, playerText, scoreText, dateText);
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        RankItemData d = getItem(position);
        if (d != null) {
            viewHolder.rank.setText(String.valueOf(d.rank));
            viewHolder.date.setText(d.time);
            viewHolder.score.setText(String.valueOf(d.score));
            viewHolder.player.setText(d.player);
//            view.setBackgroundColor(R.color.light_yellow);
            Log.e(TAG, "position: "+ position +" name:" + d.player + " rank: "+ d.rank + " isHighLight:" + d.isHighLight);

            if (selectedItems.contains(position)){
                if (d.isHighLight) {
                    setShapeColor(view, R.color.medium_yellow);
                }else {
                    setShapeColor(view, R.color.medium_blue);
                }

            }else
            {
                if (d.isHighLight) {
//                view.setBackgroundColor(R.color.light_yellow);
                    setShapeColor(view, R.color.light_yellow);
                }else {
                    setShapeColor(view, R.color.light_blue);
                }
            }

        }
//        super.
        return view;
    }

    private void setShapeColor(View itemView, int colorId) {
//         shape = itemView.findViewById(R.id.rank_item_color);
        GradientDrawable shape= (GradientDrawable) itemView
                .findViewById(R.id.rank_item_layout)
                .getBackground();
        shape.setColor(getContext().getColor(colorId));
    }
}
