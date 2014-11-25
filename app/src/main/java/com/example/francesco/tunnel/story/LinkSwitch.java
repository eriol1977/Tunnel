package com.example.francesco.tunnel.story;

/**
 * Created by Francesco on 21/11/2014.
 */
public class LinkSwitch {

    private final String sectionId;

    private final int linkIndex;

    private final String nextSection;

    private final String[] commandIds;

    private final String[] itemIds;

    public LinkSwitch(String sectionId, int linkIndex, String nextSection, String[] commandIds, String[] itemIds) {
        this.sectionId = sectionId;
        this.linkIndex = linkIndex;
        this.nextSection = nextSection;
        this.commandIds = commandIds;
        this.itemIds = itemIds;
    }

    public String getSectionId() {
        return sectionId;
    }

    public int getLinkIndex() {
        return linkIndex;
    }

    public String getNextSection() {
        return nextSection;
    }

    public String[] getCommandIds() {
        return commandIds;
    }

    public String[] getItemIds() {
        return itemIds;
    }
}
