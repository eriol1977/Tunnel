package com.example.francesco.tunnel.minigame.lockpick;

import android.graphics.Color;

import com.example.francesco.tunnel.R;

/**
 * Created by Francesco on 18/12/2014.
 */
public enum LockpickCellType {
    FAR(Color.WHITE, R.string.l_lp_far),
    NEAR(Color.YELLOW, R.string.l_lp_near),
    NEAREST(Color.RED, R.string.l_lp_nearest),
    HIT(Color.GREEN, R.string.l_lp_empty);

    private final int color;

    private final int textResourceId;

    LockpickCellType(int color, int textResourceId) {
        this.color = color;
        this.textResourceId = textResourceId;
    }

    public int getColor() {
        return color;
    }

    public int getTextResourceId() {
        return textResourceId;
    }
}
