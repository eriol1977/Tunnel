package com.example.francesco.tunnel.story;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Francesco on 05/12/2014.
 */
public class Notes {

    private final Map<String, String> notes;

    Notes() {
        this.notes = new HashMap<String, String>();
    }

    Collection<String> getIds() {
        return notes.keySet();
    }

    Collection<String> get() {
        return notes.values();
    }

    String get(final String id) {
        return this.notes.get(id);
    }

    void add(final String id, final String text) {
        this.notes.put(id, text);
    }

    void remove(final String id) {
        this.notes.remove(id);
    }

    void reset() {this.notes.clear();}
}
