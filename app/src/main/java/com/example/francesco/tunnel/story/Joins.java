package com.example.francesco.tunnel.story;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Francesco on 06/12/2014.
 */
public class Joins {

    private final List<Join> joins;

    Joins(final List<Join> joins) {
        this.joins = joins;
    }

    String getJoinResult(final String input, final Inventory inventory) {
        for (final Join join : this.joins) {
            if (join.check(input, inventory))
                return join.getTargetSectionId();
        }
        return null;
    }

    List<Join> getAvailableJoins(final Inventory inventory) {
        List<Join> available = new ArrayList<Join>();
        for (final Join join : this.joins)
            if (join.check(inventory))
                available.add(join);
        return available;
    }
}
