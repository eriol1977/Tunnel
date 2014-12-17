package com.example.francesco.tunnel.minigame.lockpick;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.example.francesco.tunnel.minigame.Minigame;

import java.util.Random;


public class Lockpick extends Activity {

    public final static String COLUMNS = "columns";

    public final static String RADIUS = "radius";

    private final static int DEFAULT_COLUMNS = 6;

    private final static int DEFAULT_RADIUS = 5;

    private Vibrator vibrator;

    private int screenWidth;

    private int screenHeight;

    private int[] cellToPick;

    private Grid grid;

    private int columns;

    private int radius;

    private String winNextSection;

    private String loseNextSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        initParams();

        grid = new Grid(this, screenWidth, screenHeight, columns, radius);
        grid.setCellToPick(random(), random());
        setContentView(grid);

        vibratorInit();
    }

    private void initParams() {
        this.columns = getIntent().getIntExtra(COLUMNS, DEFAULT_COLUMNS);
        this.radius = getIntent().getIntExtra(RADIUS, DEFAULT_RADIUS);
        this.winNextSection = getIntent().getStringExtra(Minigame.PARAM_WIN_NEXT_SECTION);
        this.loseNextSection = getIntent().getStringExtra(Minigame.PARAM_LOSE_NEXT_SECTION);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (event.getX() > 0.90 * screenWidth && event.getY() > 0.90 * screenHeight)
                    vibrator.vibrate(getVibrationPattern(10), 0);
                else if (event.getX() > 0.75 * screenWidth && event.getY() > 0.75 * screenHeight)
                    vibrator.vibrate(getVibrationPattern(20), 0);
                else if (event.getX() > 0.60 * screenWidth && event.getY() > 0.60 * screenHeight)
                    vibrator.vibrate(getVibrationPattern(30), 0);
                else
                    vibratorStop();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
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
        super.onStop();
    }
}
