package com.example.francesco.tunnel.story;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Francesco on 20/11/2014.
 */
public class Inventory {

    private List<Item> items = new ArrayList<Item>();

    void addItem(final Item item) {
        this.items.add(item);
    }

    void removeItem(final Item item) {
        this.items.remove(item);
    }

    boolean checkItem(final Item item) {
        return this.items.contains(item);
    }

    boolean checkItems(final Item[] input) {
        return this.items.containsAll(Arrays.asList(input));
    }

    List<Item> getItems() {
        return this.items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    void reset() {
        this.items.clear();
    }
}
