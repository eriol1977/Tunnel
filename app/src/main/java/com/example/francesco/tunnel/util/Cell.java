package com.example.francesco.tunnel.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

/**
 * Created by Francesco on 12/12/2014.
 */
public class Cell {

    protected final Paint paint;

    private final int line;

    private final int column;

    private final Point topLeft;

    private final Point topRight;

    private final Point bottomLeft;

    private final Point bottomRight;

    private boolean hit = false;

    public Cell(final int line, final int column, final Point[] crossingPoints) {
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

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    /**
     * @param x x di un punto sullo schermo
     * @param y y di un punto sullo schermo
     * @return True se il punto informato fa parte della presente cellula
     */
    public boolean hit(final float x, final float y) {
        return x >= topLeft.x && x <= bottomRight.x && y >= topLeft.y && y <= bottomRight.y;
    }

    public void draw(final Canvas canvas) {
        canvas.drawRect(topLeft.x + 1, topLeft.y + 1, bottomRight.x, bottomRight.y, paint);
    }

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;

        Cell cell = (Cell) o;

        if (column != cell.column) return false;
        if (line != cell.line) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = line;
        result = 31 * result + column;
        return result;
    }
}
