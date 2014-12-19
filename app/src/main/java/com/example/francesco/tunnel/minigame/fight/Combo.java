package com.example.francesco.tunnel.minigame.fight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Francesco on 19/12/2014.
 */
public class Combo implements FightAction {

    private final List<Move> moves;

    Combo(Move... moves) {
        this.moves = Arrays.asList(moves);
    }

    boolean check(final List<Integer> inputMoves) {
        boolean result = true;
        if (inputMoves.size() == moves.size()) {
            for (int i = 0; i < moves.size(); i++) {
                if (!inputMoves.get(i).equals(moves.get(i))) {
                    result = false;
                    break;
                }
            }
        } else
            result = false;
        return result;
    }

    @Override
    public List<Move> getMoves() {
        return moves;
    }

    @Override
    public int[] getResourceId(boolean attack) {
        final int[] resources = new int[this.moves.size()];
        for (int i = 0; i < moves.size(); i++)
            resources[i] = moves.get(i).getResourceId(attack)[0];
        return resources;
    }
}
