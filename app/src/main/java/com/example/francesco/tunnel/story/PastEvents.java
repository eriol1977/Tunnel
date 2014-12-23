package com.example.francesco.tunnel.story;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Francesco on 23/12/2014.
 */
public class PastEvents {
    private List<Event> events = new ArrayList<Event>();

    void addEvent(final Event event) {
        this.events.add(event);
    }

    void removeEvent(final Event event) {
        this.events.remove(event);
    }

    boolean checkEvent(final Event event) {
        return this.events.contains(event);
    }

    boolean checkEvents(final List<Event> input) {
        return this.events.containsAll(input);
    }

    List<Event> getEvents() {
        return this.events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    void reset() {
        this.events.clear();
    }
}
