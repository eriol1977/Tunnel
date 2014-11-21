package com.example.francesco.tunnel.story;

/**
 * Created by Francesco on 21/11/2014.
 */
public class Command {

    private final String id;

    private final String[] words;

    public Command(String id, String[] words) {
        this.id = id;
        this.words = words;
    }

    public String getId() {
        return id;
    }

    public boolean check(final String words) {
        // TODO
        return false;
    }
}
