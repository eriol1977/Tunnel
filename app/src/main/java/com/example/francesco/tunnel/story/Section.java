package com.example.francesco.tunnel.story;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Francesco on 18/11/2014.
 */
public class Section {

    private final String id;

    private boolean starting;

    private boolean ending;

    private List<String> text = new ArrayList<String>();

    private List<Link> links = new ArrayList<Link>();

    private List<Item> usableItems;

    private List<ParagraphSwitch> paragraphSwitches;

    private List<LinkSwitch> linkSwitches;

    private List<ItemGet> itemGets;

    private List<ItemDrop> itemDrops;

    /**
     * Default sections
     */

    public final static String HOME_SECTION = "home";

    public final static String HELP_SECTION = "help";

    public final static String END_SECTION = "end";

    public final static String QUIT_SECTION = "quit";

    public final static String UNAVAILABLE_SECTION = "unavailable";

    public final static String TEMPORARY = "temporary"; // ex: inventario, esaminare oggetto...

    Section(final String id) {
        this.id = id;
    }

    String getId() {
        return id;
    }

    /**
     * Il metodo viene chiamato ogni volta che la sezione viene invocata, svolgendo azioni
     * quali l'update di paragrafi e/o di links di altre sezioni, nonché l'aggiornamento dell'inventario
     */
    void activate() {
        StoryLoader sl = StoryLoader.getInstance();

        // entrando nella sezione, l'inventario si arricchisce di oggetti
        if (this.itemGets != null) {
            for (final ItemGet itemGet : this.itemGets) {
                sl.getCharacter().getInventory().addItem(sl.item(itemGet.getItemId()));
            }
        }

        // entrando nella sezione, l'inventario perde oggetti
        if (this.itemDrops != null) {
            for (final ItemDrop itemDrop : this.itemDrops) {
                sl.getCharacter().getInventory().removeItem(sl.item(itemDrop.getItemId()));
            }
        }

        // entrando nella sezione, alcuni paragrafi di altre sezioni vengono cambiati o rimossi
        if (this.paragraphSwitches != null) {
            sl.loadParagraphSwitches(this.paragraphSwitches);
        }

        // entrando nella sezione, alcuni link di altre sezioni vengono cambiati o rimossi
        if (this.linkSwitches != null) {
            sl.loadLinkSwitches(this.linkSwitches);
        }
    }

    /////// TEXT

    List<String> getText() {
        return text;
    }

    void setText(List<String> text) {
        this.text = text;
    }

    void addParagraph(final String paragraph) {
        this.text.add(paragraph);
    }

    void removeParagraph(final int position) {
        this.text.remove(position - 1);
    }

    void updateParagraph(final String paragraph, final int position) {
        removeParagraph(position);
        this.text.add(position - 1, paragraph);
    }

    //////// LINKS

    void addLink(final String nextSection, final String[] commandIds, final String[] itemIds) {
        final Link link = new Link(this, nextSection);
        link.setCommandIds(commandIds);
        link.setItemIds(itemIds);
        this.links.add(link);
    }

    void removeLink(final int position) {
        this.links.remove(position - 1);
    }

    void updateLink(final int position, final String nextSection, final String[] commandIds, final String[] itemIds) {
        removeLink(position);
        final Link link = new Link(this, nextSection);
        link.setCommandIds(commandIds);
        link.setItemIds(itemIds);
        this.links.add(position - 1, link);
    }

    Link getLink(final int index) {
        return this.links.get(index);
    }

    public List<Link> getLinks() {
        return links;
    }

    void setLinks(List<Link> links) {
        this.links = links;
    }

    boolean hasDirectOutcome() {
        return this.links.size() == 1 || this.isTemporary();
    }

    //////// ITEMS

    void setUsableItems(List<Item> items) {
        this.usableItems = items;
    }

    boolean checkUsableItem(final Item item) {
        return this.usableItems.contains(item);
    }

    Item checkUsableItem(final String input) {
        if (this.usableItems != null) {
            for (final Item item : this.usableItems) {
                if (item.check(input))
                    return item;
            }
        }
        return null;
    }

    void setItemsGets(List<Item> items) {
        this.itemGets = new ArrayList<ItemGet>(items.size());
        for (final Item item : items) {
            this.itemGets.add(new ItemGet(item.getId()));
        }
    }

    void setItemDrops(List<Item> items) {
        this.itemDrops = new ArrayList<ItemDrop>(items.size());
        for (final Item item : items) {
            this.itemDrops.add(new ItemDrop(item.getId()));
        }
    }

    //////// SWITCHES

    void setParagraphSwitches(final List<ParagraphSwitch> paragraphSwitches) {
        this.paragraphSwitches = paragraphSwitches;
    }

    void setLinkSwitches(final List<LinkSwitch> linkSwitches) {
        this.linkSwitches = linkSwitches;
    }

    //////// OTHERS

    boolean isStarting() {
        return starting;
    }

    void setStarting(boolean starting) {
        this.starting = starting;
    }

    boolean isEnding() {
        return ending;
    }

    void setEnding(boolean ending) {
        this.ending = ending;
    }

    boolean isTemporary() {
        return this.id.equals(Section.HELP_SECTION) || this.id.equals(Section.UNAVAILABLE_SECTION) || this.id.equals(Section.TEMPORARY);
    }
}
