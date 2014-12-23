package com.example.francesco.tunnel.story;

/**
 * Created by Francesco on 23/12/2014.
 */
public class Event {

    final private String id;

    final private String description;

    Event(String id, String description) {
        this.id = id;
        this.description = description;
    }

    String getId() {
        return id;
    }
}
