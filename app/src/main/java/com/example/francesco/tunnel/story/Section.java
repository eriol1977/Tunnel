package com.example.francesco.tunnel.story;

import com.example.francesco.tunnel.character.Item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Francesco on 18/11/2014.
 */
public class Section {

    private final String id;

    private final Story story;

    private List<String> text;

    private List<Item> items = new ArrayList<Item>();

    private List<Link> links = new ArrayList<Link>();

    private List<ParagraphSwitch> paragraphSwitches = new ArrayList<ParagraphSwitch>();

    private List<LinkSwitch> linkSwitches = new ArrayList<LinkSwitch>();

    private List<ItemGet> itemGets = new ArrayList<ItemGet>();

    private List<ItemDrop> itemDrops = new ArrayList<ItemDrop>();

    Section(final String id, Story story) {
        this.id = id;
        this.story = story;
    }

    String getId() {
        return id;
    }

    /**
     * Il metodo viene chiamato ogni volta che la sezione viene invocata, svolgendo azioni
     * quali l'update di paragrafi e/o di links di altre sezioni, nonché l'aggiornamento dell'inventario
     */
    void activate() {

    }

    /////// TEXT

    List<String> getText() {
        return text;
    }

    void setText(List<String> text) {
        this.text = text;
    }

    void addParagraph(final String paragraph, final int position)
    {
        this.text.add(position - 1, paragraph);
    }

    void removeParagraph(final int position)
    {
        this.text.remove(position);
    }

    void updateParagraph(final String paragraph, final int position) {
        removeParagraph(position);
        addParagraph(paragraph, position);
    }

    //////// LINKS

    void addLink(final String id, final String nextSection, final String[] commandIds, final String[] itemIds) {
        final Link link = new Link(id, nextSection);
        link.setCommandIds(commandIds);
        link.setItemIds(itemIds);
        links.add(link);
    }

    void removeLink(final String id) {
        Link link;
        for (Iterator<Link> iter = links.iterator(); iter.hasNext(); ) {
            link = iter.next();
            if(link.getId().equalsIgnoreCase(id)){
                iter.remove();
                break;
            }
        }
    }

    void updateLink(final String id, final String nextSection, final String[] commandIds, final String[] itemIds) {
        Link link;
        for (Iterator<Link> iter = links.iterator(); iter.hasNext(); ) {
            link = iter.next();
            if(link.getId().equalsIgnoreCase(id)){
                link.setNextSection(nextSection);
                link.setCommandIds(commandIds);
                link.setItemIds(itemIds);
                break;
            }
        }
    }

    //////// ITEMS

    List<Item> getItems() {
        return items;
    }

    void setItems(List<Item> items) {
        this.items = items;
    }

    Item getItem(final String name) throws StoryException {
        for (Item item : items) {
            if (item.getName().contains(name))
                return item;
        }
        throw new StoryException();
    }

    void removeItem(final Item item) {
        this.items.remove(item);
    }
}
