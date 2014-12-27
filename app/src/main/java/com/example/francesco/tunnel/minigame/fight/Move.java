package com.example.francesco.tunnel.minigame.fight;

import com.example.francesco.tunnel.R;

/**
 * Created by Francesco on 19/12/2014.
 */
public enum Move implements FightAction {
    MIDDLE(1, R.string.l_fi_attack_middle, R.string.l_fi_defense_middle, R.raw.shield, R.raw.shield),
    UP(2, R.string.l_fi_attack_up, R.string.l_fi_defense_up, R.raw.sword1, R.raw.swish1),
    DOWN(3, R.string.l_fi_attack_down, R.string.l_fi_defense_down, R.raw.sword1, R.raw.swish1),
    LEFT(4, R.string.l_fi_attack_left, R.string.l_fi_defense_left, R.raw.sword2, R.raw.swish2),
    RIGHT(5, R.string.l_fi_attack_right, R.string.l_fi_defense_right, R.raw.sword3, R.raw.swish3);

    private final int value;

    private final int attackResourceId;

    private final int defenseResourceId;

    private final int attackSoundResourceId;

    private final int defenseSoundResourceId;


    Move(final int value, final int attackResourceId, final int defenseResourceId, final int attackSoundResourceId, final int defenseSoundResourceId) {
        this.value = value;
        this.attackResourceId = attackResourceId;
        this.defenseResourceId = defenseResourceId;
        this.attackSoundResourceId = attackSoundResourceId;
        this.defenseSoundResourceId = defenseSoundResourceId;
    }

    int getValue() {
        return value;
    }

    public int getResourceId(final boolean attack) {
        return attack ? this.attackResourceId : this.defenseResourceId;
    }

    static Move get(final int input) {
        for (final Move move : Move.values())
            if (move.getValue() == input)
                return move;
        return null;
    }

    public int getSoundResourceId(final boolean attack) {
        return attack ? this.attackSoundResourceId : this.defenseSoundResourceId;
    }

    @Override
    public FightActionType getType() {
        return FightActionType.SINGLE_MOVE;
    }

    @Override
    public int size() {
        return 1;
    }
}
