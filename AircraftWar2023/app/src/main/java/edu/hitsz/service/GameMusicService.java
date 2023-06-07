package edu.hitsz.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import edu.hitsz.R;
import edu.hitsz.game.GameMessage;

public class GameMusicService extends Service {
    private static final String TAG = "GameMusicService";

    private MediaPlayer bgmPlayer;
    private MediaPlayer bossBgmPlayer;

    SoundPool soundPool;
    private Map<String, Integer> soundsMap;

    public GameMusicService() {

    }

    private void initSoundPool() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(audioAttributes)
                .build();
        soundsMap = new HashMap<>();
        soundsMap.put(GameMessage.object_bulletHit, soundPool.load(this, R.raw.bullet_hit,1 ));
        soundsMap.put(GameMessage.object_bomb, soundPool.load(this, R.raw.bomb_explosion,1 ));
        soundsMap.put(GameMessage.object_supply, soundPool.load(this, R.raw.get_supply,1 ));
        soundsMap.put(GameMessage.object_gameOver, soundPool.load(this, R.raw.game_over,1 ));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "GameMusicService onCreate");

        initSoundPool();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "==== MusicService onStartCommand ===");
        if (intent != null) {
            String action = intent.getStringExtra("action");
            boolean useMusic = intent.getBooleanExtra("useMusic", false);
            if (useMusic)
            {
                switch (action) {
                    case GameMessage.object_bgm :
                        setBgm();
                        break;
                    case GameMessage.object_bossBgm:
                        setBossBgm();
                        break;
                    default:
                        setSounds(action);
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void setBgm() {
        if (bgmPlayer == null) {
            bgmPlayer = MediaPlayer.create(this, R.raw.bgm);
            bgmPlayer.setLooping(true);
        }
        bgmPlayer.start();

        if (bossBgmPlayer != null && bossBgmPlayer.isPlaying()) {
            bossBgmPlayer.pause();
        }

        Log.i(TAG, "play bgm");
//        System.out.println("*****************************");
    }
    private void setBossBgm() {
        if (bossBgmPlayer == null) {
            bossBgmPlayer = MediaPlayer.create(this, R.raw.bgm_boss);
            bossBgmPlayer.setLooping(true);
        }
        bossBgmPlayer.start();

        if (bgmPlayer != null && bgmPlayer.isPlaying()) {
            bgmPlayer.pause();
        }

        Log.i(TAG, "play boss bgm");
    }

    private void stopMusic() {
        if (bossBgmPlayer != null) {
            bossBgmPlayer.stop();
        }
        if (bgmPlayer != null) {
            bgmPlayer.stop();
        }
    }

    private void setSounds(String name) {
        Integer soundId = soundsMap.get(name);
        if (soundId != null) {
            soundPool.play(soundId, 1,1,1,0,1f);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMusic();
    }
}