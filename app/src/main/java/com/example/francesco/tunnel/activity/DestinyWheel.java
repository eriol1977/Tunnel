package com.example.francesco.tunnel.activity;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.francesco.tunnel.R;

import java.net.URL;
import java.util.Random;

public class DestinyWheel extends TTSBasedActivity implements View.OnClickListener {

    private Random random = new Random();

    private TextView textView;

    private MediaPlayer wheelSound;

    private MediaPlayer winSound;

    private MediaPlayer loseSound;

    private int threshold;

    private boolean started = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destiny_wheel);

        textView = (TextView) findViewById(R.id.random);
        textView.setText("!");
        textView.setOnClickListener(this);

        wheelSound = MediaPlayer.create(this, R.raw.wheel);
        winSound = MediaPlayer.create(this, R.raw.win);
        loseSound = MediaPlayer.create(this, R.raw.lose);

        threshold = 4;
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
            spinWheel(1, 10);
        }
    }

    private void spinWheel(final int min, final int max) {
        new WheelSpinnerTask().execute(min, max, threshold, 35, 100, 5);
    }

    private void showResult(final int result) {
        speak(String.valueOf(result));
        if (result > threshold) {
            textView.setTextColor(Color.GREEN);
            winSound.start();
        } else {
            textView.setTextColor(Color.RED);
            loseSound.start();
        }
    }

    private int randomInt(int min, int max) {
        // add 1 to make it inclusive
        return random.nextInt((max - min) + 1) + min;
    }

    private class WheelSpinnerTask extends AsyncTask<Integer, Integer, Integer> {

        protected Integer doInBackground(Integer... params) {
            // inizializza i valori interni
            int min = params[0];
            int max = params[1];
            int threshold = params[2];
            int spinCycles = params[3];
            int firstSleep = params[4];
            int sleepIncrement = params[5];
            int sleep = firstSleep;
            int i = 0;
            int previousNumber = -1;
            int number = -1;

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
                    number = randomInt(1, 10);
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
