package com.example.francesco.tunnel.story;

import com.example.francesco.tunnel.character.Inventory;
import com.example.francesco.tunnel.character.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Francesco on 18/11/2014.
 */
public class StoryMaker {

    private final Story story;

    private final StringLoader sl;

    public StoryMaker(final StringLoader sl) {
        this.sl = sl;
        this.story = new Story(new StoryItems(sl));

        createSections(Story.HOME, Story.HELP, Story.END, Story.QUIT);
    }

    public Story getStory() {
        return story;
    }

    private List<String> getSectionText(final String id) {
        boolean finish = false;
        int i = 1;
        String paragraph;
        List<String> text = new ArrayList<String>();
        while (!finish) {
            paragraph = sl.s("s_" + id + "_" + i); // es: s_home_1, s_2_3
            if (paragraph == null)
                finish = true;
            else {
                text.add(paragraph);
                i++;
            }
        }
        return text;
    }

    /**
     * oggetti della sezione separati da ';'
     * ogni oggetto assume il seguente formato
     * <<id oggetto>>:<<numero del paragrafo da rimuovere quando l'oggetto viene preso>>:<<testo del paragrafo da sostituire, o string vuota
     * per rimuovere il paragrafo e basta>>
     *
     * ex: <item name="s_3_items" type="string">i_torch:3:5,i_key:1:0,i_rope:4:0</item>
     *
     * @param id
     * @return oggetti della sezione
     */
    private List<Item> getSectionItems(String id) {
        final String itemGroup = sl.s("s_" + id + "_items");
        if(itemGroup != null) {
            String[] itemStrings = itemGroup.split(";");
            List<Item> items =  new ArrayList<Item>(itemStrings.length);
            String[] tokens;
            Item item;
            for(String itemString: itemStrings) {
                tokens = itemString.split(":");
                item = story.getItems().getItem(tokens[0]);
                item.setParagraphToRemove(Integer.valueOf(tokens[1]).intValue());
                item.setParagraphToAdd(tokens[2]);
                items.add(item);
            }
            return items;
        }
        return new ArrayList<Item>();
    }

    public void createSections(final int from, final int to) {
        for (int i = from; i <= to; i++) {
            createSection(String.valueOf(i));
        }
    }

    public void createSections(final String... ids) {
        for (String id : ids) {
            createSection(id);
        }
    }

    public Section createSection(final String id) {
        final Section section = new Section(id, story);
        section.setText(getSectionText(id));
        section.setItems(getSectionItems(id));
        this.story.addSection(section);
        return section;
    }



    public void createStartingSection(final String id) {
        final Section section = createSection(id);
        section.setStarting(true);
        this.story.setStarting(section);
    }

    public void createEndingSection(final String id) {
        final Section section = createSection(id);
        section.setEnding(true);
    }

    public Section createInventorySection(final Inventory inventory, final Section current) {
        Section inventorySection = new Section("temp", current.getStory());
        final List<Item> items = inventory.getItems();
        List<String> text = new ArrayList<String>(items.size() + 1);
        if (items.isEmpty())
            text.add(sl.EMPTY_INVENTORY);
        else {
            text.add(sl.INVENTORY_CONTENT);
            for (Item item : items) {
                text.add(item.getName());
            }
        }
        inventorySection.setText(text);
        current.getStory().addSection(inventorySection);
        current.getStory().addDirectOutcome("temp", current.getId());
        return inventorySection;
    }

    public Section createItemsSection(final Section current) {
        Section itemsSection = new Section("temp", current.getStory());
        final List<Item> items = current.getItems();
        List<String> text = new ArrayList<String>(items.size() + 1);
        if (items.isEmpty())
            text.add(sl.EMPTY_SECTION_ITEMS);
        else {
            text.add(sl.SECTION_ITEMS);
            for (Item item : items) {
                text.add(item.getName());
            }
        }
        itemsSection.setText(text);
        current.getStory().addSection(itemsSection);
        current.getStory().addDirectOutcome("temp", current.getId());
        return itemsSection;
    }

    public Section createGetSection(final Section current, final String what, final Inventory inventory) {
        final List<Item> items = current.getItems();
        Item item = null;
        for(Item i: items) {
            if(what.contains(i.getName())) {
                item = i;
                break;
            }
        }

        List<String> text = new ArrayList<String>();
        if(item != null) {
            text.add(sl.s("s_" + current.getId() + "_get_" + item.getId()));
            inventory.addItem(item);
            current.removeItem(item);
            current.switchParagraph(item);
        }else{
            text.add(sl.NO_OBJECT);
        }

        Section getSection = new Section("temp", current.getStory());
        getSection.setText(text);
        current.getStory().addSection(getSection);
        current.getStory().addDirectOutcome("temp", current.getId());
        return getSection;
    }

    public void link(final String from, final String to, final String outcome) {
        this.story.addOutcome(from, to, outcome);
    }

    public void linkDirectly(final String from, final String to) {
        this.story.addDirectOutcome(from, to);
    }
}
