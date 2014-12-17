package com.example.francesco.tunnel.minigame.lockpick;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Francesco on 12/12/2014.
 */
public class Grid extends View {

    private final int w;
    private final int h;

    private final Paint linePaint;

    private final Paint circlePaint;

    private final int circleRadius;

    private final int columns;

    private List<Point> points;

    private List<Cell> cells;

    private Cell cellToPick;

    /**
     * @param context
     * @param w            larghezza dello schermo
     * @param h            altezza dello schermo
     * @param columns      numero di colonne (e linee) della grid
     * @param circleRadius raggio dei cerchi disegnati all'intersezione tra le linee della grid
     *                     (o 0 per non disegnare i cerchi)
     */
    Grid(final Context context, int w, int h, int columns, int circleRadius) {
        super(context);
        this.w = w;
        this.h = h;
        this.circleRadius = circleRadius;

        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.WHITE);

        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        circlePaint.setColor(Color.WHITE);

        this.columns = columns;
        points = getPoints();
        loadCells();
    }

    protected void onDraw(Canvas canvas) {
        if (circleRadius > 0)
            for (final Point point : points)
                canvas.drawCircle(point.x, point.y, circleRadius, circlePaint);

        Path path = buildPath();
        canvas.drawPath(path, linePaint);
    }

    private Path buildPath() {
        Path path = new Path();
        final int columnsPlusOne = columns + 1;
        int z = (columnsPlusOne * columnsPlusOne) - columnsPlusOne;

        // disegna le colonne
        int j = 0;
        while (j <= z) {
            path.moveTo(points.get(j).x, points.get(j).y);
            for (int i = j + 1; i <= j + columns; i++)
                path.lineTo(points.get(i).x, points.get(i).y);
            j += columnsPlusOne;
        }
        // disegna le righe
        j = 0;
        while (j <= columns) {
            path.moveTo(points.get(j).x, points.get(j).y);
            for (int i = j + columnsPlusOne; i <= j + z; i += columnsPlusOne)
                path.lineTo(points.get(i).x, points.get(i).y);
            j++;
        }
        return path;
    }

    private List<Point> getPoints() {
        // i punti vengono immagazzinati per colonna: prima tutti quelli della prima colonna,
        // dall'alto in basso, poi quelli della seconda colonna, della terza, e così via
        final int columnsPlusOne = columns + 1;
        final List<Point> localPoints = new ArrayList<Point>(columnsPlusOne * columnsPlusOne);
        int xStep = w / columns;
        int yStep = h / columns;
        for (int x = 1; x <= w + 1; x += xStep) {
            for (int y = 1; y <= h + 1; y += yStep) {
                localPoints.add(new Point(x, y));
            }
        }
        // aggiusta i punti dell'ultima colonna e riga affinché non escano dallo schermo
        int z = (columnsPlusOne * columnsPlusOne) - columnsPlusOne;
        int q = (columnsPlusOne * columnsPlusOne) - 1;
        for (int k = z; k <= z + columns; k++)
            localPoints.get(k).set(localPoints.get(k).x - 2, localPoints.get(k).y);
        for (int k = columns; k <= q; k += columnsPlusOne)
            localPoints.get(k).set(localPoints.get(k).x, localPoints.get(k).y - 2);

        return localPoints;
    }

    /**
     * Ritorna i quattro punti posti all'incrocio tra linea e colonna informati,
     * nel seguente ordine:
     * - in alto a sinistra
     * - in alto a destra
     * - in basso a sinistra
     * - in basso a destra
     */
    private Point[] getCrossingPoints(final int line, final int column) {
        final Point[] crossing = new Point[4];
        int indexTopLeft = ((column - 1) * (this.columns + 1)) + (line - 1);
        int indexTopRight = (column * (this.columns + 1)) + (line - 1);
        int indexBottomLeft = ((column - 1) * (this.columns + 1)) + line;
        int indexBottomRight = (column * (this.columns + 1)) + line;
        crossing[0] = this.points.get(indexTopLeft);
        crossing[1] = this.points.get(indexTopRight);
        crossing[2] = this.points.get(indexBottomLeft);
        crossing[3] = this.points.get(indexBottomRight);
        return crossing;
    }

    private void loadCells() {
        this.cells = new ArrayList<Cell>(this.columns * this.columns);
        for (int line = 1; line <= this.columns; line++) {
            for (int col = 1; col <= this.columns; col++) {
                this.cells.add(new Cell(line, col, getCrossingPoints(line, col)));
            }
        }
    }

    Cell getCellToPick() {
        return cellToPick;
    }

    void setCellToPick(final int line, final int col) {
        this.cellToPick = getCell(line, col);
    }

    Cell getCell(final int line, final int col) {
        for (final Cell cell : this.cells)
            if (cell.getLine() == line && cell.getColumn() == col)
                return cell;
        return null;
    }

    /**
     * @param x x di un punto sullo schermo
     * @param y y di un punto sullo schermo
     * @return cella della grid corrispondente alle coordinate informate
     */
    Cell getCell(final float x, final float y) {
        for (final Cell cell : this.cells)
            if (cell.hit(x, y))
                return cell;
        return null;
    }

}