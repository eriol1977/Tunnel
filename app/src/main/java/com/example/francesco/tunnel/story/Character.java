package com.example.francesco.tunnel.story;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Francesco on 20/11/2014.
 */
public class Character {

    private final Inventory inventory;

    private final Notes notes;

    public Character() {
        this.inventory = new Inventory();
        this.notes = new Notes();
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Notes getNotes() {
        return notes;
    }

    void reset() {
        this.inventory.reset();
        this.notes.reset();
    }
}
