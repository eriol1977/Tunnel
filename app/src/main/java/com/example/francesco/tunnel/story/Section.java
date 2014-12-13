package com.example.francesco.tunnel.story;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Francesco on 18/11/2014.
 */
public class Section {

    private final String id;

    private boolean starting;

    private boolean ending;

    private boolean temporary;

    private List<String> text = new ArrayList<String>();

    private List<Link> links = new ArrayList<Link>();

    private List<ParagraphSwitch> paragraphSwitches;

    private List<LinkSwitch> linkSwitches;

    private List<ItemGet> itemGets;

    private List<ItemDrop> itemDrops;

    /**
     * Default sections
     */

    public final static String HOME_SECTION = "home";

    public final static String HELP_SECTION = "help";

    final static String END_SECTION = "end";

    final static String QUIT_SECTION = "quit";

    final static String UNAVAILABLE_SECTION = "unavailable";

    final static String LOADING = "loading";

    final static String SAVING = "saving";

    final static String INVENTORY = "inventory";

    final static String TEMPORARY = "temporary"; // ex: esaminare oggetto...

    final static String ARE_YOU_SURE = "are_you_sure";

    Section(final String id) {
        this.id = id;
    }

    public String getId() {
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
            this.itemGets.clear();
        }

        // entrando nella sezione, l'inventario perde oggetti
        if (this.itemDrops != null) {
            for (final ItemDrop itemDrop : this.itemDrops) {
                sl.getCharacter().getInventory().removeItem(sl.item(itemDrop.getItemId()));
            }
            this.itemDrops.clear();
        }

        // entrando nella sezione, alcuni paragrafi di altre sezioni vengono cambiati o rimossi
        if (this.paragraphSwitches != null) {
            sl.loadParagraphSwitches(this.paragraphSwitches);
            this.paragraphSwitches.clear();
        }

        // entrando nella sezione, alcuni link di altre sezioni vengono cambiati o rimossi
        if (this.linkSwitches != null) {
            sl.loadLinkSwitches(this.linkSwitches);
            this.linkSwitches.clear();
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

    void addLink(final String linkId, final String nextSection, final String[] commandIds, final String[] itemIds) {
        final Link link = new Link(linkId, this, nextSection);
        link.setCommandIds(commandIds);
        link.setItemIds(itemIds);
        this.links.add(link);
    }

    void removeLink(final String id) {
        final Link link = getLink(id);
        if (link != null)
            this.links.remove(link);
    }

    void updateLink(final String id, final String nextSection, final String[] commandIds, final String[] itemIds) {
        removeLink(id);
        addLink(id, nextSection, commandIds, itemIds);
    }

    Link getFirstLink() {
        return this.links.get(0);
    }

    Link getLink(final String id) {
        for (final Link link : this.links)
            if (link.getId().equals(id))
                return link;
        return null;
    }

    public List<Link> getLinks() {
        return links;
    }

    void setLinks(List<Link> links) {
        this.links = links;
    }

    boolean hasDirectOutcome() {
        return this.links.size() == 1;
    }

    void clearLinks() {
        this.links.clear();
    }

    //////// ITEMS

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
        return temporary;
    }

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }
}
