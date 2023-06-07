package edu.hitsz.activity;


import static edu.hitsz.activity.LoginActivity.PORT;
import static edu.hitsz.activity.LoginActivity.getIpAddr;
import static edu.hitsz.utils.LogUtils.logException;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.hitsz.R;
import edu.hitsz.game.BaseGame;
import edu.hitsz.game.EasyGame;
import edu.hitsz.game.GameMessage;
import edu.hitsz.game.HardGame;
import edu.hitsz.game.MediumGame;
import edu.hitsz.service.GameMusicService;
import edu.hitsz.utils.params.GameMode;
import edu.hitsz.utils.rank.IRankCollectorDAO;
import edu.hitsz.utils.rank.RankCollectors;
import edu.hitsz.utils.rank.RankDataStruct;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";

    private BaseGame baseGameView;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean isOnline;
    private boolean getIsOnline() {
        return isOnline;
    }
    private int gameType;
    private String usrname;

    private int getGameType() {
        return gameType;
    }

    private boolean useMusic;
    private boolean getUseMusic(){
        return useMusic;
    }

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        useMusic = getIntent().getBooleanExtra("useMusic", false);
        isOnline = getIntent().getBooleanExtra("isOnline", false);
        usrname = getIntent().getStringExtra("usrname");


        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case GameMessage.what_gameover:
                        gameOver(msg);
                        break;
                    case GameMessage.what_setMusic:
                        setMusic(msg);
                        break;
                    case GameMessage.what_score:
//                        Log.e(TAG, msg.obj.toString());
                        sendScoreToServer(msg);
                        break;
                    case GameMessage.what_hp:
                        sendHpToServer(msg);
                        break;
                    case GameMessage.what_setOtherHp:
                        baseGameView.setOtherPlayerHp((int)msg.obj);
                        break;
                    case GameMessage.what_setOtherScore:
                        baseGameView.setOtherPlayerScore((int)msg.obj);
                        break;
                    case GameMessage.what_startGame:
                        setContentView(baseGameView);
                        baseGameView.setName(usrname, (String) msg.obj);
                    default:
                        break;
                }
            }
        };
        gameType = getIntent().getIntExtra("gameType",GameMode.EASY);

        if(gameType == GameMode.MEDIUM) {
            baseGameView = new MediumGame(this,handler, getIsOnline());
        }else if(gameType == GameMode.HARD) {
            baseGameView = new HardGame(this,handler, getIsOnline());
        }else{
            baseGameView = new EasyGame(this,handler, getIsOnline());
        }
        if (!getIsOnline()) {
            // 单机，直接开始
            setContentView(baseGameView);
        }else {
            setContentView(R.layout.activity_game_wait);
            MainActivity.threadPool.execute(getOnlineTask());
        }

    }


    private int rankSave(int score, String playerName) {
        IRankCollectorDAO dao = RankCollectors.getRankCollector(getGameType());
        int res = dao.add(new RankDataStruct(playerName, score, new Date()));
        dao.save();
        return res;
    }

    private void toRankTable(JSONObject dataJSON, int thisRank) {
        Intent intent = new Intent(GameActivity.this, RankTableActivity.class);
        intent.putExtra("gameType", getGameType());
        intent.putExtra("isOnline", isOnline);
        intent.putExtra("thisRank", thisRank);
        if (dataJSON != null) {
            Log.i(TAG, dataJSON.toString());
            intent.putExtra("data", dataJSON.toString());
            Log.i(TAG,"结束");
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                logException(TAG, e);
            }
        }

        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void getName(int score) {

        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

        builder.setTitle("你的得分是: "+score+"\n请输入你的大名");

        EditText inputText = new EditText(GameActivity.this);
        builder.setView(inputText);

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String playerName = inputText.getText().toString();
                int rank = rankSave(score, playerName);
                toRankTable(null, rank);
            }
        });
        builder.show();
    }

    private void gameOver(Message msg) {

        Toast.makeText(GameActivity.this,"GameOver",Toast.LENGTH_SHORT).show();
        stopService(new Intent(GameActivity.this, GameMusicService.class));
        int score = (int)msg.obj;
        if (getIsOnline()) {
            onlineGameOver(score);
        }else {
            getName(score);
        }
    }

    @SuppressLint({"ResourceType", "SetTextI18n"})
    private void onlineGameOver(int score) {
        Runnable r = () -> {
            try {

                JSONObject json = new JSONObject();
                json.put("score", score);
                json.put("hp", 0);
                json.put("end", true);
                out.writeUTF(json.toString());
                out.flush();


            } catch (JSONException | IOException e) {
                logException(TAG, e);
            }
        };
        MainActivity.threadPool.execute(r);

        setContentView(R.layout.activity_game_wait);
        TextView textView = findViewById(R.id.game_wait_text);
        textView.setText("游戏结束\n你的得分是:"+score+"\n等待对手完成");
    }

    private void setMusic(Message msg) {
        String action = (String) msg.obj;
        Intent intent = new Intent(GameActivity.this, GameMusicService.class);
        intent.putExtra("action", action);
        intent.putExtra("useMusic", getUseMusic());
//        Log.e(TAG, action);
        startService(intent);
    }

//    private Thread oThread;

    private Runnable getOnlineTask() {
        return () -> {

            ;
            try {
                Log.i(TAG, "IP: "+getIpAddr());
                socket = new Socket(getIpAddr(), PORT);
                out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                String otherName = waitLogin(in, out);

                MainActivity.threadPool.execute(getOtherPlayerDataTask(in, out));

                sendMessage(GameMessage.what_startGame, otherName);
//                setContentView(baseGameView);


            } catch (IOException | JSONException e) {
                logException(TAG, e);
                finish();
            }


        };
    }

    private Runnable getOtherPlayerDataTask(DataInputStream in, DataOutputStream out) {
        return () -> {
            try {
                boolean isLoop = true;
                do {
                    JSONObject json = new JSONObject(in.readUTF());
                    boolean isEnd = json.getBoolean("end");

                    Log.i(TAG, "receive json: "+json.toString());
                    isLoop = !isEnd;
                    if(isEnd) {

                        toRankTable(json, -1);
                        break;
                    }
                    if (json.has("otherHp")) {
//                        baseGameView.setOtherPlayerHp(json.getInt("otherHp"));
                        sendMessage(GameMessage.what_setOtherHp, json.getInt("otherHp"));

                    } else if (json.has("otherScore")) {
//                        baseGameView.setOtherPlayerScore(json.getInt("otherScore"));
                        sendMessage(GameMessage.what_setOtherScore, json.getInt("otherScore"));
                    }
                }while (isLoop);

//                System.out.println(new JSONObject(in.readUTF()));



            } catch (IOException | JSONException e) {
                logException(TAG, e);
            }
        };
    }

    private String waitLogin(DataInputStream in, DataOutputStream out) throws JSONException, IOException {
        JSONObject json = new JSONObject();

        json.put("name", usrname);
        json.put("op", 3);
//                json.put("password", "qwe112rtgaa");
        json.put("gameType", gameType);
        String jsonString = json.toString();

        Log.i(TAG, "发送: "+jsonString);
        out.writeUTF(jsonString);
        out.flush();
//
        Log.i(TAG, "发送成功");
//                System.out.println(in.readUTF());
        boolean isMatch = false;
        String otherName = "";
        do {
            JSONObject j = new JSONObject(in.readUTF());
            isMatch = j.getBoolean("match");
            if (j.has("otherName")) {
                otherName = j.getString("otherName");
            }
            System.out.println(j);
        }while (!isMatch);

        return otherName;
    }

    private void sendScoreToServer(Message msg) {
        int score = (int) msg.obj;
        if (!getIsOnline()) {
            return;
        }

        Runnable r = () -> {
            try {
                JSONObject json = new JSONObject();
                json.put("score", score);
                json.put("end", false);
                out.writeUTF(json.toString());
                out.flush();
            } catch (IOException | JSONException e) {
                logException(TAG, e);
            }
        };

        MainActivity.threadPool.execute(r);



    }

    private void sendHpToServer(Message msg) {
        int hp = (int) msg.obj;
        if (!getIsOnline()) {
            return;
        }

        Runnable r = () -> {

            try {
                JSONObject json = new JSONObject();
                json.put("hp", hp);
                json.put("end", false);
                out.writeUTF(json.toString());
                out.flush();
            } catch (IOException | JSONException e) {
                logException(TAG, e);
            }
        };

        MainActivity.threadPool.execute(r);

    }

    private void sendMessage(int what, Object obj) {
        Message message = Message.obtain();
        message.what = what ;
        message.obj = obj;
        handler.sendMessage(message);
    }




    public void main2() {

        ExecutorService threadPool = Executors.newCachedThreadPool();
        try {
            Socket socket = new Socket(LoginActivity.getIpAddr(), PORT);
            System.out.println("connect success");

            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            for (int i = 0; i<1; i++) {
                JSONObject json = new JSONObject();

                json.put("name", "weishen2");
                json.put("op", 3);
//                json.put("password", "qwe112rtgaa");
                json.put("gameType", 0);
                String jsonString = json.toString();

                System.out.println("***********");
                System.out.println("发送次数: "+i );
                System.out.println("发送长度: "+jsonString.length());
                out.writeUTF(jsonString);
                out.flush();
//
                System.out.println("发送成功");
//                System.out.println(in.readUTF());
                boolean isMatch = false;
                do {
                    JSONObject j = new JSONObject(in.readUTF());
                    isMatch = j.getBoolean("match");
                    System.out.println(j);
                }while (!isMatch);

//                Thread.sleep(1000);
            }

            /**
             * 接收
             */
            Runnable getOtherScore = () -> {
                try {
                    boolean isLoop = true;
                    do {
                        JSONObject json = new JSONObject(in.readUTF());
                        boolean isEnd = json.getBoolean("end");
                        isLoop = !isEnd;
                        System.out.println(json);
                    }while (isLoop);


                } catch (IOException | JSONException e) {
                    logException(TAG, e);
                }
            };

            Thread thread = new Thread(getOtherScore);
            thread.start();


            /**
             * 开始游戏
             */
//            for (int i=0;i<5;i++) {
//                Thread.sleep(300+ new Random().nextInt(200));
//                JSONObject json = new JSONObject();
//                json.put("score", i*10);
//                json.put("end", false);
//                out.writeUTF(json.toString());
//                out.flush();
//
//                Thread.sleep(50);
//                json = new JSONObject();
//                json.put("hp", i*111);
//                json.put("end", false);
//                out.writeUTF(json.toString());
//                out.flush();
//            }

            /**
             * 结束游戏
             */

            JSONObject json = new JSONObject();
            json.put("score", 900);
            json.put("end", true);
            out.writeUTF(json.toString());
            out.flush();

            /**
             * 获取排行榜
             */

//            System.out.println(new JSONObject(in.readUTF()));

            thread.join();
            System.out.println("结束");
//            out.close();
//            in.close();
            try {
                socket.close();
            }catch (IOException ignored){

            }


        } catch (IOException | JSONException | InterruptedException e) {
            logException(TAG, e);
        }
    }
}