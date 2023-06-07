package edu.hitsz.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.R;
import edu.hitsz.utils.params.GameMode;
import edu.hitsz.utils.rank.RankCollectors;

public class SelectGameModeActivity extends AppCompatActivity {

    private static final String TAG = "SelectGameModeActivity";

    public static int screenWidth;
    public static int screenHeight;


    private int gameType=0;
    private String usrname;
    private boolean useMusic = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_gamemode);

        RankCollectors.init(this);

        Button return_btn = findViewById(R.id.select_page_return);
        Button medium_btn = findViewById(R.id.medium_btn);
        Button easy_btn = findViewById(R.id.easy_btn);
        Button hard_btn = findViewById(R.id.hard_btn);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch music_open = findViewById(R.id.music_open);

        getScreenHW();

        boolean isOnline = getIntent().getBooleanExtra("isOnline",false);
        usrname = getIntent().getStringExtra("usrname");

        Intent intent = new Intent(SelectGameModeActivity.this, GameActivity.class);
        intent.putExtra("isOnline", isOnline);
        intent.putExtra("usrname", usrname);

        music_open.setChecked(true);
        music_open.setOnCheckedChangeListener((buttonView, isChecked) -> useMusic = isChecked);

        medium_btn.setOnClickListener(view -> {
            gameType= GameMode.MEDIUM;
            intent.putExtra("gameType",gameType);
            intent.putExtra("useMusic",useMusic);
            startActivity(intent);
        });

        easy_btn.setOnClickListener(view -> {
            gameType = GameMode.EASY;
            intent.putExtra("gameType",gameType);
            intent.putExtra("useMusic",useMusic);
            startActivity(intent);
        });

        hard_btn.setOnClickListener(view -> {
            gameType = GameMode.HARD;
            intent.putExtra("gameType",gameType);
            intent.putExtra("useMusic",useMusic);
            startActivity(intent);
        });

        return_btn.setOnClickListener(view -> {
            finish();
        });
    }
    public void getScreenHW(){
        //定义DisplayMetrics 对象
        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //窗口的宽度
        screenWidth= dm.widthPixels;
        //窗口高度
        screenHeight = dm.heightPixels;


        Log.i(TAG, "screenWidth : " + screenWidth + " screenHeight : " + screenHeight);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}