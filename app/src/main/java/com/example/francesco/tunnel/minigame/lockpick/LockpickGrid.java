package com.example.francesco.tunnel.minigame.lockpick;

import android.content.Context;

import com.example.francesco.tunnel.util.Cell;
import com.example.francesco.tunnel.util.Grid;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Francesco on 18/12/2014.
 */
public class LockpickGrid extends Grid {
    /**
     * @param context
     * @param w            larghezza dello schermo
     * @param h            altezza dello schermo
     * @param columns      numero di colonne (e linee) della grid
     * @param circleRadius raggio dei cerchi disegnati all'intersezione tra le linee della grid
     */
    public LockpickGrid(Context context, int w, int h, int columns, int circleRadius) {
        super(context, w, h, columns, circleRadius);
    }

    @Override
    protected Cell buildCell(int line, int col) {
        return new LockPickCell(line, col, getCrossingPoints(line, col));
    }

    @Override
    public void setCellToHit(int line, int col) {
        super.setCellToHit(line, col);
        ((LockPickCell) cellToHit).setType(LockpickCellType.HIT);
        setCellTypes(line, col);
    }

    private void setCellTypes(final int hitLine, final int hitCol) {
        List<Integer> nearLines = new ArrayList<>();
        for (int i = -2; i <= 2; i++)
            nearLines.add(hitLine + i);
        List<Integer> nearestLines = new ArrayList<>();
        for (int i = -1; i <= 1; i++)
            nearestLines.add(hitLine + i);
        List<Integer> nearCols = new ArrayList<>();
        for (int i = -2; i <= 2; i++)
            nearCols.add(hitCol + i);
        List<Integer> nearestCols = new ArrayList<>();
        for (int i = -1; i <= 1; i++)
            nearestCols.add(hitCol + i);
        for (final Cell cell : cells) {
            if (!cell.equals(cellToHit)) {
                if (nearestLines.contains(cell.getLine()) && nearestCols.contains(cell.getColumn()))
                    ((LockPickCell) cell).setType(LockpickCellType.NEAREST);
                else if (nearLines.contains(cell.getLine()) && nearCols.contains(cell.getColumn()))
                    ((LockPickCell) cell).setType(LockpickCellType.NEAR);
                else
                    ((LockPickCell) cell).setType(LockpickCellType.FAR);
            }
        }
    }
}
