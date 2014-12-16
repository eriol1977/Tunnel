package com.example.francesco.tunnel.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.francesco.tunnel.R;

import java.io.IOException;
import java.util.Random;

public class DestinyWheel extends TTSBasedActivity implements View.OnClickListener {

    private Random random = new Random();

    private TextView textView;

    private MediaPlayer mediaPlayer;

    private int treshold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destiny_wheel);

        textView = (TextView) findViewById(R.id.random);
        textView.setText("!");
        textView.setOnClickListener(this);

        mediaPlayer = MediaPlayer.create(this, R.raw.wheel);

        treshold = 5;
    }

    @Override
    public void onInit(int status) {
        super.onInit(status);
        speak(getResources().getString(R.string.l_dw_welcome));
        speak(getResources().getString(R.string.l_dw_greater_then) + treshold);
    }

    @Override
    public void onClick(View v) {
        tts.stop();
        spinWheel(1, 10);
    }

    private void spinWheel(final int min, final int max) {
        WheelSpinner spinner = new WheelSpinner(min, max, treshold, 35, 100, 5);
        spinner.start();
    }

    class WheelSpinner extends Thread {

        private final int min;

        private final int max;

        private final int treshold;

        private final int spinCycles;

        private final int firstSleep;

        private final int sleepIncrement;

        WheelSpinner(final int min, final int max, final int treshold, final int spinCycles, final int firstSleep, final int sleepIncrement) {
            this.min = min;
            this.max = max;
            this.treshold = treshold;
            this.spinCycles = spinCycles;
            this.firstSleep = firstSleep;
            this.sleepIncrement = sleepIncrement;
        }

        @Override
        public void run() {
            int previousNumber = -1;
            try {
                int i = 0;
                WheelSpin wheelSpin;
                int sleep = firstSleep;
                mediaPlayer.start();
                while (i < spinCycles) {
                    Thread.sleep(sleep);
                    wheelSpin = new WheelSpin(min, max, previousNumber);
                    runOnUiThread(wheelSpin);
                    previousNumber = wheelSpin.getNumber();
                    i++;
                    sleep += sleepIncrement;
                }
            } catch (InterruptedException e) {
            }
            speak(getResources().getString(R.string.l_dw_extraction) + previousNumber);
            if (previousNumber >= treshold)
                speak(getResources().getString(R.string.l_dw_win));
            else
                speak(getResources().getString(R.string.l_dw_lose));
        }
    }

    class WheelSpin implements Runnable {

        private final int min;

        private final int max;

        private int number = -1;

        WheelSpin(final int min, final int max, final int previousNumber) {
            this.min = min;
            this.max = max;
            do {
                number = randomInt(1, 10);
            } while (number == previousNumber);
        }

        @Override
        public void run() {
            textView.setText(String.valueOf(number));
        }

        private int randomInt(int min, int max) {
            // add 1 to make it inclusive
            return random.nextInt((max - min) + 1) + min;
        }

        int getNumber() {
            return number;
        }
    }

    @Override
    protected void onStop() {
        mediaPlayer.release();
        mediaPlayer = null;
        super.onStop();
    }
}
