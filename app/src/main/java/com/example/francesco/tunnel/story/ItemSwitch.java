package com.example.francesco.tunnel.story;

/**
 * Created by Francesco on 06/12/2014.
 */
public class ItemSwitch {

    private final String itemId;

    private final String newDescription;

    private final String newNoteId;

    ItemSwitch(String itemId, String newDescription, String newNoteId) {
        this.itemId = itemId;
        this.newDescription = newDescription;
        this.newNoteId = newNoteId;
    }

    String getItemId() {
        return itemId;
    }

    String getNewDescription() {
        return newDescription;
    }

    String getNewNoteId() {
        return newNoteId;
    }
}
