package com.example.francesco.tunnel.minigame.lockpick;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.example.francesco.tunnel.R;
import com.example.francesco.tunnel.minigame.Minigame;
import com.example.francesco.tunnel.minigame.MinigameActivity;
import com.example.francesco.tunnel.util.TTSBacked;
import com.example.francesco.tunnel.util.TTSUtil;

import java.util.Random;


public class Lockpick extends MinigameActivity {

    public final static String ATTEMPTS = "maxAttempts";

    public final static String COLUMNS = "columns";

    private final static int DEFAULT_ATTEMPTS = 6;

    private final static int DEFAULT_COLUMNS = 4;

    private final static int DEFAULT_RADIUS = 5;

    private Vibrator vibrator;

    private int screenWidth;

    private int screenHeight;

    private LockpickGrid grid;

    private int maxAttempts;

    private int attempts = 0;

    private int columns;

    private MediaPlayer lockpickSound;

    private MediaPlayer unlockSound;

    private MediaPlayer lockbreakSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initScreenDimensions();

        grid = new LockpickGrid(this, screenWidth, screenHeight, columns, DEFAULT_RADIUS);
        grid.setCellToHit(random(), random());
        setContentView(grid);

        vibratorInit();
    }

    private void initScreenDimensions() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
    }

    protected void initGameParams() {
        this.maxAttempts = getIntent().getIntExtra(ATTEMPTS, DEFAULT_ATTEMPTS);
        this.columns = getIntent().getIntExtra(COLUMNS, DEFAULT_COLUMNS);
    }

    protected void initGameSounds() {
        lockpickSound = MediaPlayer.create(this, R.raw.lockpick);
        unlockSound = MediaPlayer.create(this, R.raw.unlock);
        lockbreakSound = MediaPlayer.create(this, R.raw.lockbreak);
    }

    protected void showWinning() {
        vibratorStop();
        unlockSound.start();
    }

    protected void showLosing() {
        vibratorStop();
        lockbreakSound.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (finished) {
                    sendBackResult();
                    return true;
                }

                if (!started) {
                    ttsUtil.stop();
                    started = true;
                }

                final LockPickCell cell = (LockPickCell) grid.hit(event.getX(), event.getY());
                // se il quadrante non era ancora stato toccato
                if (!cell.isHit()) {
                    cell.setHit(true);
                    lockpickSound.start();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    vibrator.vibrate(getVibrationPattern(cell.getType().getVibrationSpeed()), 0);
                    attempts++;
                    grid.invalidate();
                    if (cell.isWinning())
                        win();
                    else if (attempts == maxAttempts)
                        lose();
                    // se il quadrante era già stato toccato, ripete semplicemente la vibrazione ad
                    // esso corrispondente, come indizio verso la soluzione
                } else {
                    vibrator.vibrate(getVibrationPattern(cell.getType().getVibrationSpeed()), 0);
                }
                break;
        }
        return true;
    }

    private int random() {
        final Random random = new Random();
        // add 1 to make it inclusive
        return random.nextInt(this.columns) + 1;
    }

    private long[] getVibrationPattern(final int speed) {
        if (speed == 0)
            vibratorStop();
        return new long[]{speed, speed};
    }

    private void vibratorInit() {
        this.vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (!vibrator.hasVibrator()) {
            finish();
        }
    }

    private void vibratorStop() {
        if (vibrator != null)
            vibrator.cancel();
    }

    @Override
    protected void onStop() {
        vibratorStop();
        lockpickSound.release();
        unlockSound.release();
        lockbreakSound.release();
        lockpickSound = null;
        unlockSound = null;
        lockbreakSound = null;
        super.onStop();
    }

    @Override
    public void afterTTSInit() {
        speak(R.string.l_lp_attempts, String.valueOf(maxAttempts));
        speak(R.string.l_lp_help);
        speak(R.string.l_lp_start);
    }
}