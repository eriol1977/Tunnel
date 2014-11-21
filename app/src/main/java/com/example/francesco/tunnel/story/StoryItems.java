package com.example.francesco.tunnel.story;

import com.example.francesco.tunnel.character.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Francesco on 20/11/2014.
 */
public class StoryItems {

    private final Map<String, Item> items = new HashMap<String, Item>();

    private final StringLoader sl;

    public StoryItems(StringLoader sl) {
        this.sl = sl;
        loadItems();
    }

    private void loadItems() {
        final Map<String, String> pairs = sl.getKeyValuePairsStartingWithPrefix("i_");
        String[] nameAndDescription;
        for (String key : pairs.keySet()) {
            nameAndDescription = pairs.get(key).split("-");
            items.put(key, new Item(key, nameAndDescription[0], nameAndDescription[1]));
        }
    }

    public Item getItem(final String id) {
        return items.get(id);
    }

    public List<Item> getItems(final String... ids) {
        List<Item> found = new ArrayList<Item>();
        Item item;
        for (String id : ids) {
            item = getItem(id);
            if (item != null)
                found.add(item);
        }
        return found;
    }

}
