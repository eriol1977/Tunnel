package com.example.francesco.tunnel.minigame.wheel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.francesco.tunnel.R;
import com.example.francesco.tunnel.minigame.Minigame;
import com.example.francesco.tunnel.minigame.MinigameActivity;
import com.example.francesco.tunnel.util.TTSBacked;
import com.example.francesco.tunnel.util.TTSUtil;

import java.util.Random;

public class DestinyWheel extends MinigameActivity implements View.OnClickListener {

    public final static String MIN = "min";

    public final static String MAX = "max";

    public final static String THRESHOLD = "threshold";

    private final static int DEFAULT_MIN = 1;

    private final static int DEFAULT_MAX = 10;

    private final static int DEFAULT_THRESHOLD = 4;

    private final static int SPIN_CYCLES = 35;

    private final static int FIRST_SLEEP = 100;

    private final static int SLEEP_INCREMENT = 5;

    private Random random = new Random();

    private TextView textView;

    private MediaPlayer wheelSound;

    private int min;

    private int max;

    private int threshold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destiny_wheel);

        textView = (TextView) findViewById(R.id.random);
        textView.setText("!");
        textView.setOnClickListener(this);
    }

    protected void initGameParams() {
        this.min = getIntent().getIntExtra(MIN, DEFAULT_MIN);
        this.max = getIntent().getIntExtra(MAX, DEFAULT_MAX);
        this.threshold = getIntent().getIntExtra(THRESHOLD, DEFAULT_THRESHOLD);
    }

    protected void initGameSounds() {
        wheelSound = MediaPlayer.create(this, R.raw.wheel);
    }

    @Override
    public void onClick(View v) {
        if (!started) {
            ttsUtil.stop();
            started = true;
            spinWheel();
        } else if (finished) {
            ttsUtil.stop();
            sendBackResult();
        }
    }

    protected void showWinning() {
        textView.setTextColor(Color.GREEN);
    }

    protected void showLosing() {
        textView.setTextColor(Color.RED);
    }

    @Override
    public void afterTTSInit() {
        ttsUtil.speak(getResources().getString(R.string.l_dw_greater_then) + threshold);
        ttsUtil.speak(getResources().getString(R.string.l_dw_start));
    }

    private void spinWheel() {
        new WheelSpinnerTask().execute(SPIN_CYCLES, FIRST_SLEEP, SLEEP_INCREMENT);
    }

    private class WheelSpinnerTask extends AsyncTask<Integer, Integer, Integer> {

        protected Integer doInBackground(Integer... params) {
            // inizializza i valori interni
            int spinCycles = params[0];
            int firstSleep = params[1];
            int sleepIncrement = params[2];
            int sleep = firstSleep;
            int i = 0;
            int previousNumber = -1;
            int number;

            // gira la ruota
            wheelSound.start();
            while (i < spinCycles) {
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // sceglie sempre un numero differente dal precedente
                do {
                    number = randomInt(min, max);
                } while (number == previousNumber);
                publishProgress(number);

                // aggiorna i valori interni
                previousNumber = number;
                i++;
                sleep += sleepIncrement;
            }
            return previousNumber;
        }

        protected void onProgressUpdate(Integer... progress) {
            textView.setText(String.valueOf(progress[0]));
        }

        protected void onPostExecute(Integer result) {
            ttsUtil.speak(String.valueOf(result));
            if (result > threshold)
                win();
            else
                lose();
        }

        private int randomInt(int min, int max) {
            // add 1 to make it inclusive
            return random.nextInt((max - min) + 1) + min;
        }
    }

    @Override
    protected void onStop() {
        wheelSound.release();
        wheelSound = null;
        super.onStop();
    }

}
