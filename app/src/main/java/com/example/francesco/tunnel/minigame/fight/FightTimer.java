package com.example.francesco.tunnel.minigame.fight;

import android.os.CountDownTimer;
import android.util.Log;

/**
 * Created by Francesco on 20/12/2014.
 */
public class FightTimer extends CountDownTimer {

    private final Fight fight;

    /**
     * @param millisInFuture The number of millis in the future from the call
     *                       to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                       is called.
     */
    public FightTimer(final Fight fight, final long millisInFuture) {
        super(millisInFuture, millisInFuture);
        this.fight = fight;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        // do nothing
    }

    @Override
    public void onFinish() {
        fight.checkInput();
    }
}
