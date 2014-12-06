package com.example.francesco.tunnel.story;

import java.util.List;

/**
 * Created by Francesco on 06/12/2014.
 */
public class Joins {

    private final List<Join> joins;

    Joins(final List<Join> joins) {
        this.joins = joins;
    }

    List<Join> get() {
        return this.joins;
    }
}
