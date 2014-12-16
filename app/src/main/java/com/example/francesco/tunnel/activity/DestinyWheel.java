package com.example.francesco.tunnel.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.francesco.tunnel.R;

import java.net.URL;
import java.util.Random;

public class DestinyWheel extends TTSBasedActivity implements View.OnClickListener {

    public final static String RESULT = "result";

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

    private MediaPlayer winSound;

    private MediaPlayer loseSound;

    private int min;

    private int max;

    private int threshold;

    private boolean started = false;

    private boolean finished = false;

    private int result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destiny_wheel);

        textView = (TextView) findViewById(R.id.random);
        textView.setText("!");
        textView.setOnClickListener(this);

        initSounds();

        this.min = getIntent().getIntExtra(MIN, DEFAULT_MIN);
        this.max = getIntent().getIntExtra(MAX, DEFAULT_MAX);
        this.threshold = getIntent().getIntExtra(THRESHOLD, DEFAULT_THRESHOLD);
    }

    private void initSounds() {
        wheelSound = MediaPlayer.create(this, R.raw.wheel);
        winSound = MediaPlayer.create(this, R.raw.win);
        loseSound = MediaPlayer.create(this, R.raw.lose);
        winSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                speak(getResources().getString(R.string.l_dw_finish));
            }
        });
        loseSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                speak(getResources().getString(R.string.l_dw_finish));
            }
        });
    }

    @Override
    public void onInit(int status) {
        super.onInit(status);
        speak(getResources().getString(R.string.l_dw_greater_then) + threshold);
        speak(getResources().getString(R.string.l_dw_welcome));
    }

    @Override
    public void onClick(View v) {
        if (!started) {
            tts.stop();
            started = true;
            spinWheel();
        } else if (finished) {
            sendBackResult();
        }
    }

    private void spinWheel() {
        new WheelSpinnerTask().execute(SPIN_CYCLES, FIRST_SLEEP, SLEEP_INCREMENT);
    }

    private void showResult(final int result) {
        this.result = result;
        this.finished = true;
        speak(String.valueOf(result));
        if (result > threshold) {
            textView.setTextColor(Color.GREEN);
            winSound.start();
        } else {
            textView.setTextColor(Color.RED);
            loseSound.start();
        }
    }

    private void sendBackResult() {
        Intent resultIntent = new Intent("com.example.RESULT_ACTION");
        resultIntent.putExtra(RESULT, this.result);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
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
            showResult(result.intValue());
        }

        private int randomInt(int min, int max) {
            // add 1 to make it inclusive
            return random.nextInt((max - min) + 1) + min;
        }
    }

    @Override
    protected void onStop() {
        wheelSound.release();
        winSound.release();
        loseSound.release();
        wheelSound = null;
        winSound = null;
        loseSound = null;
        super.onStop();
    }
}
