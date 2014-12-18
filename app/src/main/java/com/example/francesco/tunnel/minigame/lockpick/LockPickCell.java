package com.example.francesco.tunnel.minigame.lockpick;

import android.graphics.Point;

import com.example.francesco.tunnel.util.Cell;

/**
 * Created by Francesco on 18/12/2014.
 */
public class LockPickCell extends Cell {

    private LockpickCellType type;

    LockPickCell(int line, int column, Point[] crossingPoints) {
        super(line, column, crossingPoints);
    }

    public LockpickCellType getType() {
        return type;
    }

    void setType(LockpickCellType type) {
        this.type = type;
    }

    @Override
    public boolean hit(float x, float y) {
        final boolean hit = super.hit(x, y);
        if (hit)
            this.paint.setColor(type.getColor());
        return hit;
    }

    public boolean isWinning() {
        return type.equals(LockpickCellType.HIT);
    }
}
