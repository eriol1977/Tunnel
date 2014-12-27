package com.example.francesco.tunnel.minigame.fight;

import com.example.francesco.tunnel.util.LimitedList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Francesco on 27/12/2014.
 */
public class Combo implements FightAction {

    private final List<Move> moves;

    Combo(final int size) {
        moves = new ArrayList<>(size);
    }

    void addMove(final Move move) {
        this.moves.add(move);
    }

    public int size() {
        return this.moves.size();
    }

    Move get(final int index) {
        return this.moves.get(index);
    }

    @Override
    public FightActionType getType() {
        return FightActionType.COMBO;
    }
}
