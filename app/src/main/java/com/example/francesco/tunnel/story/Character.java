package com.example.francesco.tunnel.story;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Francesco on 20/11/2014.
 */
public class Character {

    private final Inventory inventory;

    public Character() {
        this.inventory = new Inventory();
    }

    public Inventory getInventory() {
        return inventory;
    }

    void reset() {
        this.inventory.reset();
    }

    boolean hasItem(final Item item) {
        return this.inventory.checkItem(item);
    }
}
