package com.example.francesco.tunnel.character;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Francesco on 20/11/2014.
 */
public class Inventory {

    private List<Item> items = new ArrayList<Item>();

    public void addItem(final Item item) {
        this.items.add(item);
    }

    public void removeItem(final Item item) {
        this.items.remove(item);
    }

    public boolean checkItem(final Item item) {
        return this.items.contains(item);
    }

    public List<Item> getItems() {
        return items;
    }
}
