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

    public final static String RADIUS = "radius";

    private final static int DEFAULT_ATTEMPTS = 5;

    private final static int DEFAULT_COLUMNS = 6;

    private final static int DEFAULT_RADIUS = 5;

    private Vibrator vibrator;

    private int screenWidth;

    private int screenHeight;

    private LockpickGrid grid;

    private int maxAttempts;

    private int attempts = 0;

    private int columns;

    private int radius;

    private String winNextSection;

    private String loseNextSection;

    private String result;

    private MediaPlayer winSound;

    private MediaPlayer loseSound;

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

        grid = new LockpickGrid(this, screenWidth, screenHeight, columns, radius);
        grid.setCellToHit(random(), random());
        setContentView(grid);

        vibratorInit();
    }

    private void initParams() {
        this.maxAttempts = getIntent().getIntExtra(ATTEMPTS, DEFAULT_ATTEMPTS);
        this.columns = getIntent().getIntExtra(COLUMNS, DEFAULT_COLUMNS);
        this.radius = getIntent().getIntExtra(RADIUS, DEFAULT_RADIUS);
        this.winNextSection = getIntent().getStringExtra(Minigame.PARAM_WIN_NEXT_SECTION);
        this.loseNextSection = getIntent().getStringExtra(Minigame.PARAM_LOSE_NEXT_SECTION);
    }

    private void initSounds() {
        winSound = MediaPlayer.create(this, R.raw.win);
        loseSound = MediaPlayer.create(this, R.raw.lose);
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
        winSound.start();
    }

    private void lose() {
        this.finished = true;
        this.result = this.loseNextSection;
        vibratorStop();
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

                attempts++;
                final LockPickCell cell = (LockPickCell) grid.hit(event.getX(), event.getY());
                vibrator.vibrate(getVibrationPattern(cell.getType().getVibrationSpeed()), 0);
                grid.invalidate();
                if (cell.isWinning())
                    win();
                else if (attempts == maxAttempts)
                    lose();
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
        winSound = null;
        loseSound = null;
        super.onStop();
    }

    @Override
    public void afterTTSInit() {
        ttsUtil.speak(getResources().getString(R.string.l_lp_attempts) + maxAttempts);
        ttsUtil.speak(getResources().getString(R.string.l_lp_welcome));
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }
}
