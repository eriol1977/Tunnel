package com.example.francesco.tunnel.minigame;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.example.francesco.tunnel.R;
import com.example.francesco.tunnel.util.TTSBacked;
import com.example.francesco.tunnel.util.TTSUtil;

/**
 * Created by Francesco on 18/12/2014.
 */
public abstract class MinigameActivity extends Activity implements TTSBacked {

    protected MediaPlayer winSound;

    protected MediaPlayer loseSound;

    protected boolean started = false;

    protected boolean finished = false;

    protected String result;

    protected String winNextSection;

    protected String loseNextSection;

    protected TTSUtil ttsUtil;

    protected int screenWidth;

    protected int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initParams();

        initSounds();

        ttsUtil = new TTSUtil(this, this);
    }

    private void initParams() {
        this.winNextSection = String.valueOf(getIntent().getIntExtra(Minigame.PARAM_WIN_NEXT_SECTION, 0));
        this.loseNextSection = String.valueOf(getIntent().getIntExtra(Minigame.PARAM_LOSE_NEXT_SECTION, 0));
        initGameParams();
    }

    protected abstract void initGameParams();

    private void initSounds() {
        winSound = MediaPlayer.create(this, R.raw.win);
        loseSound = MediaPlayer.create(this, R.raw.lose);
        initGameSounds();
    }

    protected abstract void initGameSounds();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ttsUtil.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void win() {
        this.finished = true;
        this.result = this.winNextSection;
        showWinning();
        winSound.start();
    }

    protected abstract void showWinning();

    protected void lose() {
        this.finished = true;
        this.result = this.loseNextSection;
        showLosing();
        loseSound.start();
    }

    protected abstract void showLosing();

    protected void sendBackResult() {
        Intent resultIntent = new Intent("com.example.RESULT_ACTION");
        resultIntent.putExtra(Minigame.RESULT_NEXT_SECTION, this.result);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    protected void speak(final int resourceId, final String param) {
        ttsUtil.speak(msg(resourceId) + " " + param);
    }

    protected void speak(final String param, final int resourceId) {
        ttsUtil.speak(param + " " + msg(resourceId));
    }

    protected void speak(final int resourceId) {
        ttsUtil.speak(msg(resourceId));
    }

    protected void speak(int... resources) {
        for (int resourceId : resources)
            ttsUtil.speak(msg(resourceId));
    }

    protected String msg(final int resourceId) {
        return getResources().getString(resourceId);
    }

    @Override
    protected void onRestart() {
        ttsUtil.onRestart();
        super.onRestart();
    }

    @Override
    protected void onStop() {
        ttsUtil.onStop();
        winSound.release();
        loseSound.release();
        winSound = null;
        loseSound = null;
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }
}
