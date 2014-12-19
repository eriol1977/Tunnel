package com.example.francesco.tunnel.minigame.fight;

import java.util.List;

/**
 * Created by Francesco on 19/12/2014.
 */
public interface FightAction {

    List<Move> getMoves();

    int[] getResourceId(final boolean attack);
}
