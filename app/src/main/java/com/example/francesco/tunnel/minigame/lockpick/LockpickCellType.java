package com.example.francesco.tunnel.minigame.lockpick;

import android.graphics.Color;

/**
 * Created by Francesco on 18/12/2014.
 */
public enum LockpickCellType {
    FAR(Color.WHITE, 0), NEAR(Color.YELLOW, 30), NEAREST(Color.RED, 10), HIT(Color.GREEN, 0);

    private final int color;

    private final int vibrationSpeed;


    LockpickCellType(int color, int vibrationSpeed) {
        this.color = color;
        this.vibrationSpeed = vibrationSpeed;
    }

    public int getColor() {
        return color;
    }

    public int getVibrationSpeed() {
        return vibrationSpeed;
    }
}
