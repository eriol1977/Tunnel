package com.example.francesco.tunnel.story;

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
}
