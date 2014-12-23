package com.example.francesco.tunnel.story;

/**
 * Created by Francesco on 20/11/2014.
 */
public class Character {

    private final Inventory inventory;

    private final PastEvents pastEvents;

    public Character() {
        this.inventory = new Inventory();
        this.pastEvents = new PastEvents();
    }

    public Inventory getInventory() {
        return inventory;
    }

    public PastEvents getPastEvents() {
        return pastEvents;
    }

    void reset() {
        this.inventory.reset();
        this.pastEvents.reset();
    }

    boolean hasItem(final Item item) {
        return this.inventory.checkItem(item);
    }

    boolean hasPastEvent(final Event event) {
        return this.pastEvents.checkEvent(event);
    }
}
