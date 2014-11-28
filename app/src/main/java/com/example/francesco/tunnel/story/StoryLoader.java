package com.example.francesco.tunnel.story;

import com.example.francesco.tunnel.activity.StoryTellerActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Francesco on 24/11/2014.
 */
public class StoryLoader {

    private static StoryLoader instance;

    private final StoryTellerActivity activity;

    private final Story story;

    private final Character character;

    private final Commands commands;

    private final Items items;

    private final static String COMMAND_PREFIX = "c_";

    private final static String ITEM_PREFIX = "i_";

    private final static String SECTIONS = "sections";

    private final static String SECTION_PREFIX = "s_";

    private final static String SECTION_USABLEITEM_SUFFIX = "_usable";

    private final static String SECTION_ITEMGET_SUFFIX = "_get";

    private final static String SECTION_ITEMDROP_SUFFIX = "_drop";

    private final static String SECTION_LINK_SUFFIX = "_link";

    private final static String SECTION_SWITCH_SUFFIX = "_switch";

    private final static String SECTION_SWITCH_KIND_PARAGRAPH = "par";

    private final static String SECTION_SWITCH_KIND_LINK = "link";

    private final static String HOME_SECTION_PREFIX = "home_";

    private final static String HELP_SECTION_PREFIX = "help_";

    private final static String END_SECTION_PREFIX = "end_";

    private final static String QUIT_SECTION_PREFIX = "quit_";

    private final static String SEPARATOR = ":";

    private final static String LIST_SEPARATOR = ",";

    private final static String NO_PREFIX = "no_";

    private StoryLoader(StoryTellerActivity activity) {
        this.activity = activity;
        this.character = new Character();
        this.story = new Story(this.character);
        commands = loadCommands();
        items = loadItems();
    }

    void resetStory() {
        this.character.getInventory().reset();
        loadDefaultSections();
        this.story.setSections(loadSections());
    }

    public static StoryLoader getInstance(final StoryTellerActivity activity) {
        if (instance == null)
            instance = new StoryLoader(activity);
        return instance;
    }

    /**
     * Attenzione! Da usare soltanto dopo aver istanziato correttamente l'oggetto con
     * getInstance(final StoryTellerActivity activity)
     *
     * @return Singleton
     */
    public static StoryLoader getInstance() {
        return instance;
    }

    //////// LOADERS

    private Commands loadCommands() {
        final Map<String, String> pairs = activity.getKeyValuePairsStartingWithPrefix(COMMAND_PREFIX);
        final Map<String, Command> commands = new HashMap<String, Command>(pairs.size());
        String[] words;
        for (String key : pairs.keySet()) {
            words = pairs.get(key).split("\\s+");
            commands.put(key, new Command(key, words));
        }
        return new Commands(commands);
    }

    private Items loadItems() {
        final Map<String, String> pairs = activity.getKeyValuePairsStartingWithPrefix(ITEM_PREFIX);
        final Map<String, Item> items = new HashMap<String, Item>(pairs.size());
        String[] nameAndDescription;
        for (String key : pairs.keySet()) {
            nameAndDescription = pairs.get(key).split(SEPARATOR);
            items.put(key, new Item(key, nameAndDescription[0], nameAndDescription[1]));
        }
        return new Items(items);
    }

    private void loadDefaultSections() {
        loadHomeSection();
        loadHelpSection();
        loadEndSection();
        loadQuitSection();
        loadUnavailableSection();
    }

    private void loadHomeSection() {
        final Section home = new Section(Section.HOME_SECTION);
        home.setText(loadSectionText(HOME_SECTION_PREFIX));
        this.story.addSection(home);
    }

    private void loadHelpSection() {
        final Section help = new Section(Section.HELP_SECTION);
        help.setText(loadSectionText(HELP_SECTION_PREFIX));
        this.story.addSection(help);
    }

    private void loadEndSection() {
        final Section end = new Section(Section.END_SECTION);
        end.setText(loadSectionText(END_SECTION_PREFIX));
        this.story.addSection(end);
    }

    private void loadQuitSection() {
        final Section quit = new Section(Section.QUIT_SECTION);
        quit.setText(loadSectionText(QUIT_SECTION_PREFIX));
        this.story.addSection(quit);
    }

    private void loadUnavailableSection() {
        final Section unavailable = new Section(Section.UNAVAILABLE_SECTION);
        List<String> text = new ArrayList<String>(1);
        text.add(msg(Messages.UNAVAILABLE));
        unavailable.setText(text);
        this.story.addSection(unavailable);
    }

    private List<Section> loadSections() {
        List<Section> sections = new ArrayList<Section>();
        final int sectionQty = Integer.valueOf(msg(SECTIONS)).intValue();
        String id;
        Section section;
        for (int i = 1; i <= sectionQty; i++) {
            id = String.valueOf(i);
            section = new Section(id);
            if (i == 1)
                section.setStarting(true);
            if (i == sectionQty)
                section.setEnding(true);
            section.setText(loadSectionText(SECTION_PREFIX + id + "_"));
            section.setLinks(loadSectionLinks(section));
            section.setUsableItems(loadSectionUsableItems(id));
            section.setItemsGets(loadSectionItemGets(id));
            section.setItemDrops(loadSectionItemDrops(id));
            section.setParagraphSwitches(loadSectionParagraphSwitches(id));
            section.setLinkSwitches(loadSectionLinkSwitches(id));
            sections.add(section);
        }
        return sections;
    }


    private List<String> loadSectionText(final String prefix) {
        boolean finish = false;
        int i = 1;
        String paragraph;
        List<String> text = new ArrayList<String>();
        while (!finish) {
            paragraph = msg(prefix + i); // es: s_home_1, s_2_3
            if (paragraph == null)
                finish = true;
            else {
                text.add(paragraph);
                i++;
            }
        }
        return text;
    }

    private List<Item> loadSectionUsableItems(String id) {
        final String itemGroup = msg(SECTION_PREFIX + id + SECTION_USABLEITEM_SUFFIX); // es: s_6_usable
        if (itemGroup != null) {
            String[] ids = itemGroup.split(LIST_SEPARATOR);
            return items(ids);
        }
        return new ArrayList<Item>();
    }

    private List<Item> loadSectionItemGets(String id) {
        final String itemGroup = msg(SECTION_PREFIX + id + SECTION_ITEMGET_SUFFIX); // es: s_4_get
        if (itemGroup != null) {
            String[] ids = itemGroup.split(LIST_SEPARATOR);
            return items(ids);
        }
        return new ArrayList<Item>();
    }

    private List<Item> loadSectionItemDrops(String id) {
        final String itemGroup = msg(SECTION_PREFIX + id + SECTION_ITEMDROP_SUFFIX); // es: s_5_drop
        if (itemGroup != null) {
            String[] ids = itemGroup.split(LIST_SEPARATOR);
            return items(ids);
        }
        return new ArrayList<Item>();
    }

    private List<Link> loadSectionLinks(final Section section) {
        final Map<String, String> sectionLinks = activity.getKeyValuePairsStartingWithPrefix(SECTION_PREFIX + section.getId() + SECTION_LINK_SUFFIX);// es: s_4_link
        if (sectionLinks != null) {
            List<Link> links = new ArrayList<Link>(sectionLinks.size());
            String sectionLink;
            String[] linkInfo;
            Link link;
            List<String> orderedKeys = new ArrayList<String>(sectionLinks.keySet());
            Collections.sort(orderedKeys);
            for (String linkId : orderedKeys) {

                // es: s_6_link_3 --> 9:c_across,c_corridor:i_torch
                sectionLink = sectionLinks.get(linkId);
                linkInfo = sectionLink.split(SEPARATOR);
                link = new Link(section, linkInfo[0]); // s_6_link_3, 9

                // commands
                if (linkInfo.length > 1) {
                    link.setCommandIds(linkInfo[1].split(LIST_SEPARATOR)); // c_across,c_corridor
                }

                // items
                if (linkInfo.length > 2) {
                    String[] its = linkInfo[2].split(LIST_SEPARATOR);
                    List<String> itemIds = new ArrayList<String>();
                    List<String> noItemIds = new ArrayList<String>();
                    for (String itemId : its) {
                        if (itemId.startsWith(NO_PREFIX)) {
                            noItemIds.add(itemId.substring(NO_PREFIX.length(), itemId.length())); // no_i_torch
                        } else {
                            itemIds.add(itemId); // i_torch
                        }
                    }
                    link.setItemIds(itemIds);
                    link.setNoItemIds(noItemIds);
                }

                links.add(link);
            }
            return links;
        }
        return new ArrayList<Link>();
    }

    private List<ParagraphSwitch> loadSectionParagraphSwitches(final String id) {
        final Map<String, String> sectionSwitches = activity.getKeyValuePairsStartingWithPrefix(SECTION_PREFIX + id + SECTION_SWITCH_SUFFIX);// es: s_4_switch
        if (sectionSwitches != null) {
            List<ParagraphSwitch> switches = new ArrayList<ParagraphSwitch>();
            String sectionSwitch;
            String[] switchInfo;
            for (String switchId : sectionSwitches.keySet()) {
                // ex: s_5_switch_2 --> par:1:3:"Davanti a te c\'è una porta aperta."
                sectionSwitch = sectionSwitches.get(switchId);
                switchInfo = sectionSwitch.split(SEPARATOR);
                if (switchInfo[0].equals(SECTION_SWITCH_KIND_PARAGRAPH)) {
                    switches.add(new ParagraphSwitch(switchInfo[1], Integer.valueOf(switchInfo[2]).intValue(), switchInfo.length == 4 ? switchInfo[3] : ""));
                }
            }
            return switches;
        }
        return new ArrayList<ParagraphSwitch>();
    }

    private List<LinkSwitch> loadSectionLinkSwitches(final String id) {
        final Map<String, String> sectionSwitches = activity.getKeyValuePairsStartingWithPrefix(SECTION_PREFIX + id + SECTION_SWITCH_SUFFIX);// es: s_4_switch
        if (sectionSwitches != null) {
            List<LinkSwitch> switches = new ArrayList<LinkSwitch>();
            String sectionSwitch;
            String[] switchInfo;
            for (String switchId : sectionSwitches.keySet()) {
                // ex: s_5_switch_1 --> link:1:2:6:c_go_on,c_left:i_key,i_torch
                sectionSwitch = sectionSwitches.get(switchId);
                switchInfo = sectionSwitch.split(SEPARATOR);
                if (switchInfo[0].equals(SECTION_SWITCH_KIND_LINK)) {
                    switches.add(new LinkSwitch(switchInfo[1],
                            Integer.valueOf(switchInfo[2]).intValue(),
                            switchInfo.length > 3 ? switchInfo[3] : "",
                            switchInfo.length > 4 ? switchInfo[4].split(LIST_SEPARATOR) : new String[]{},
                            switchInfo.length > 5 ? switchInfo[5].split(LIST_SEPARATOR) : new String[]{}));
                }
            }
            return switches;
        }
        return new ArrayList<LinkSwitch>();
    }

    ///////// SPECIAL SECTIONS

    Section createInventorySection(final Section current) {
        final List<Item> items = this.character.getInventory().getItems();
        List<String> text = new ArrayList<String>(items.size() + 1);
        if (items.isEmpty())
            text.add(msg(Messages.EMPTY_INVENTORY));
        else {
            text.add(msg(Messages.INVENTORY));
            for (Item item : items) {
                text.add(item.getName());
            }
        }
        return createTemporarySection(text, current);
    }

    Section createExamineSection(final Section current, final String input) {
        List<Item> items = this.character.getInventory().getItems();
        List<String> text = new ArrayList<String>(1);
        for (final Item item : items) {
            if (item.check(input)) {
                text.add(item.getDescription());
            }
        }
        if (text.isEmpty()) {
            Item item = current.checkUsableItem(input);
            if (item != null)
                text.add(item.getDescription());
        }
        if (text.isEmpty()) {
            text.add(msg(Messages.CANT_EXAMINE));
        }
        return createTemporarySection(text, current);
    }

    public Section createAvailableActionsSection(final Section current) {
        final List<Link> links = current.getLinks();
        List<String> text = new ArrayList<String>(links.size());
        String[] commandIds;
        for(final Link link: links) {
            commandIds = link.getCommandIds();
            for(final String commandId: commandIds) {
                text.add(command(commandId).getCommandWords());
            }
        }
        return createTemporarySection(text, current);
    }

    /**
     * @param text
     * @param current
     * @return sezione dotata di testo che torna sempre direttamente alla sezione attuale
     */
    private Section createTemporarySection(final List<String> text, final Section current) {
        Section tempSection = new Section(Section.TEMPORARY);
        tempSection.setText(text);
        tempSection.addLink(current.getId(), null, null);
        return tempSection;
    }

    ///////// GETTERS

    String msg(final String key) {
        return activity.s(key);
    }

    public Command command(final String id) {
        return this.commands.get(id);
    }

    public List<Command> commands(final String... ids) {
        return this.commands.get(ids);
    }

    public Item item(final String id) {
        return this.items.get(id);
    }

    public List<Item> items(final String... ids) {
        return this.items.get(ids);
    }

    public Story getStory() {
        return story;
    }

    public Character getCharacter() {
        return character;
    }



}
