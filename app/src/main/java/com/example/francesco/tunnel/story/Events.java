package com.example.francesco.tunnel.story;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Francesco on 23/12/2014.
 */
public class Events {
    private final Map<String, Event> events;

    public Events(Map<String, Event> events) {
        this.events = events;
    }

    public Event get(final String id) {
        return events.get(id);
    }

    public List<Event> get(final String... ids) {
        List<Event> found = new ArrayList<Event>();
        Event event;
        for (String id : ids) {
            event = get(id);
            if (event != null)
                found.add(event);
        }
        return found;
    }
}
