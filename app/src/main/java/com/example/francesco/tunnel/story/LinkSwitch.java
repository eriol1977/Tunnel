package com.example.francesco.tunnel.story;

/**
 * Created by Francesco on 21/11/2014.
 */
public class LinkSwitch {

    private final String sectionId;

    private final String linkId;

    private final String nextSection;

    private final String[] commandIds;

    private final String[] itemIds;

    LinkSwitch(final String sectionId, final String linkId, final String nextSection, final String[] commandIds, final String[] itemIds) {
        this.sectionId = sectionId;
        this.linkId = linkId;
        this.nextSection = nextSection;
        this.commandIds = commandIds;
        this.itemIds = itemIds;
    }

    String getSectionId() {
        return sectionId;
    }

    String getLinkId() {
        return linkId;
    }

    String getNextSection() {
        return nextSection;
    }

    String[] getCommandIds() {
        return commandIds;
    }

    String[] getItemIds() {
        return itemIds;
    }
}
