package com.example.francesco.tunnel.story;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Francesco on 24/11/2014.
 */
public class Items {

    private final Map<String, Item> items;

    public Items(Map<String, Item> items) {
        this.items = items;
    }

    public Item get(final String id) {
        return items.get(id);
    }

    public List<Item> get(final String... ids) {
        List<Item> found = new ArrayList<Item>();
        Item item;
        for (String id : ids) {
            item = get(id);
            if (item != null)
                found.add(item);
        }
        return found;
    }
}
