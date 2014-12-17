package com.example.francesco.tunnel.minigame.lockpick;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

/**
 * Created by Francesco on 12/12/2014.
 */
public class Cell {

    private final Paint paint;

    private final int line;

    private final int column;

    private final Point topLeft;

    private final Point topRight;

    private final Point bottomLeft;

    private final Point bottomRight;

    Cell(final int line, final int column, final Point[] crossingPoints) {
        this.paint = new Paint();
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setColor(Color.BLACK);
        this.line = line;
        this.column = column;
        this.topLeft = crossingPoints[0];
        this.topRight = crossingPoints[1];
        this.bottomLeft = crossingPoints[2];
        this.bottomRight = crossingPoints[3];
    }

    int getLine() {
        return line;
    }

    int getColumn() {
        return column;
    }

    /**
     * @param x x di un punto sullo schermo
     * @param y y di un punto sullo schermo
     * @return True se il punto informato fa parte della presente cellula
     */
    boolean hit(final float x, final float y) {
        return x >= topLeft.x && x <= bottomRight.x && y >= topLeft.y && y <= bottomRight.y;
    }
}
