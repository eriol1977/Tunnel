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
import com.example.francesco.tunnel.util.TTSBacked;
import com.example.francesco.tunnel.util.TTSUtil;

import java.util.Random;


public class Lockpick extends Activity implements TTSBacked {

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

    private String winNextSection;

    private String loseNextSection;

    private String result;

    private MediaPlayer winSound;

    private MediaPlayer loseSound;

    private MediaPlayer lockpickSound;

    private MediaPlayer unlockSound;

    private MediaPlayer lockbreakSound;

    private boolean started = false;

    private boolean finished = false;

    private TTSUtil ttsUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        initParams();
        initSounds();
        ttsUtil = new TTSUtil(this, this);

        grid = new LockpickGrid(this, screenWidth, screenHeight, columns, DEFAULT_RADIUS);
        grid.setCellToHit(random(), random());
        setContentView(grid);

        vibratorInit();
    }

    private void initParams() {
        this.maxAttempts = getIntent().getIntExtra(ATTEMPTS, DEFAULT_ATTEMPTS);
        this.columns = getIntent().getIntExtra(COLUMNS, DEFAULT_COLUMNS);
        this.winNextSection = getIntent().getStringExtra(Minigame.PARAM_WIN_NEXT_SECTION);
        this.loseNextSection = getIntent().getStringExtra(Minigame.PARAM_LOSE_NEXT_SECTION);
    }

    private void initSounds() {
        winSound = MediaPlayer.create(this, R.raw.win);
        loseSound = MediaPlayer.create(this, R.raw.lose);
        lockpickSound = MediaPlayer.create(this, R.raw.lockpick);
        unlockSound = MediaPlayer.create(this, R.raw.unlock);
        lockbreakSound = MediaPlayer.create(this, R.raw.lockbreak);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ttsUtil.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void win() {
        this.finished = true;
        this.result = this.winNextSection;
        vibratorStop();
        unlockSound.start();
        winSound.start();
    }

    private void lose() {
        this.finished = true;
        this.result = this.loseNextSection;
        vibratorStop();
        lockbreakSound.start();
        loseSound.start();
    }

    private void sendBackResult() {
        Intent resultIntent = new Intent("com.example.RESULT_ACTION");
        resultIntent.putExtra(Minigame.RESULT_NEXT_SECTION, this.result);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
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
    protected void onRestart() {
        super.onRestart();
        ttsUtil.onRestart();
    }

    @Override
    protected void onStop() {
        vibratorStop();
        ttsUtil.onStop();
        winSound.release();
        loseSound.release();
        lockpickSound.release();
        unlockSound.release();
        lockbreakSound.release();
        winSound = null;
        loseSound = null;
        lockpickSound = null;
        unlockSound = null;
        lockbreakSound = null;
        super.onStop();
    }

    @Override
    public void afterTTSInit() {
        ttsUtil.speak(getResources().getString(R.string.l_lp_attempts) + maxAttempts);
        ttsUtil.speak(getResources().getString(R.string.l_lp_help));
        ttsUtil.speak(getResources().getString(R.string.l_lp_start));
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }
}
