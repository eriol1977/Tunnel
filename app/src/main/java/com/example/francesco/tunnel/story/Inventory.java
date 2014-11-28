package com.example.francesco.tunnel.story;

import java.util.ArrayList;
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

    List<Item> getItems() {
        return this.items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    void reset() {
        this.items.clear();
    }

    /**
     * @return ex "i_key,i_torch,i_ring"
     */
    public String getItemIds() {
        final StringBuilder sb = new StringBuilder();
        if (!this.items.isEmpty()) {
            for (final Item item : this.items) {
                sb.append(item.getId()).append(StoryLoader.LIST_SEPARATOR);
            }
            sb.delete(sb.length() - StoryLoader.LIST_SEPARATOR.length(), sb.length());
        }
        return sb.toString();
    }
}
