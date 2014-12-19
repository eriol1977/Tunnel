package com.example.francesco.tunnel.minigame.fight;

import com.example.francesco.tunnel.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Francesco on 19/12/2014.
 */
public enum Move implements FightAction {
    MIDDLE(1, R.string.l_fi_attack_middle, R.string.l_fi_defense_middle),
    UP(2, R.string.l_fi_attack_up, R.string.l_fi_defense_up),
    DOWN(3, R.string.l_fi_attack_down, R.string.l_fi_defense_down),
    LEFT(4, R.string.l_fi_attack_left, R.string.l_fi_defense_left),
    RIGHT(5, R.string.l_fi_attack_right, R.string.l_fi_defense_right);

    private final int value;

    private final int attackResourceId;

    private final int defenseResourceId;

    Move(final int value, int attackResourceId, int defenseResourceId) {
        this.value = value;
        this.attackResourceId = attackResourceId;
        this.defenseResourceId = defenseResourceId;
    }

    int getValue() {
        return value;
    }

    public List<Move> getMoves() {
        final List<Move> moves = new ArrayList<>(1);
        moves.add(this);
        return moves;
    }

    public int[] getResourceId(final boolean attack) {
        return attack ? new int[]{this.attackResourceId} : new int[]{this.defenseResourceId};
    }

    static Move get(final int input) {
        for (final Move move : Move.values())
            if (move.getValue() == input)
                return move;
        return null;
    }
}
