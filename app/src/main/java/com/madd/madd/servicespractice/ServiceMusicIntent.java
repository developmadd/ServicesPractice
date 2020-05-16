package com.madd.madd.servicespractice;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

public class ServiceMusicIntent extends IntentService {


    MediaPlayer mediaPlayer;

    public ServiceMusicIntent() {
        super("ServiceMusic");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this,R.raw.song_game);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(100,10);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        mediaPlayer.start();
        Log.i("8A", intent.getStringExtra("Bruno"));
        for( int i = 0 ; i < 5 ; i++){
            task();
        }
    }

    void task() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if( mediaPlayer.isPlaying() ){
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        mediaPlayer= null;
        Log.i("8A","Final");
    }





}
