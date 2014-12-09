package com.example.francesco.tunnel.story;

import com.example.francesco.tunnel.activity.StoryTellerActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

    private final Joins joins;

    private final List<ParagraphSwitch> paragraphSwitchesSoFar = new ArrayList<ParagraphSwitch>();

    private final List<LinkSwitch> linkSwitchesSoFar = new ArrayList<LinkSwitch>();

    private final static String COMMAND_PREFIX = "c_";

    private final static String ITEM_PREFIX = "i_";

    private final static String JOIN_PREFIX = "j_";

    private final static String SECTIONS = "sections";

    private final static String STARTING = "starting";

    private final static String ENDING = "ending";

    private final static String SECTION_PREFIX = "s_";

    private final static String SECTION_ITEMGET_SUFFIX = "_get";

    private final static String SECTION_ITEMDROP_SUFFIX = "_drop";

    private final static String SECTION_NOTES_DROP_SUFFIX = "_notes_drop";

    private final static String SECTION_NOTES_GET_SUFFIX = "_notes_get";

    private final static String SECTION_LINK_SUFFIX = "_link";

    private final static String SECTION_SWITCH_SUFFIX = "_switch";

    private final static String SECTION_SWITCH_KIND_PARAGRAPH = "par";

    private final static String SECTION_SWITCH_KIND_LINK = "link";

    private final static String SECTION_SWITCH_KIND_ITEM = "item";

    private final static String HOME_SECTION_PREFIX = "home_";

    private final static String HELP_SECTION_PREFIX = "help_";

    private final static String END_SECTION_PREFIX = "end_";

    private final static String QUIT_SECTION_PREFIX = "quit_";

    private final static String SEPARATOR = ":";

    private final static String STRONG_SEPARATOR = ";";

    private final static String LIST_SEPARATOR = ",";

    private final static String NO_PREFIX = "no_";

    private StoryLoader(StoryTellerActivity activity) {
        this.activity = activity;
        this.character = new Character();
        this.story = new Story(this.character);
        commands = loadCommands();
        items = loadItems();
        joins = loadJoins();
    }

    void resetStory() {
        this.story.reset();
        this.character.reset();
        loadDefaultSections();
        this.story.setSections(loadSections());
        this.paragraphSwitchesSoFar.clear();
        this.linkSwitchesSoFar.clear();
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
        String[] nameAndSection;
        for (String key : pairs.keySet()) {
            nameAndSection = pairs.get(key).split(SEPARATOR);
            items.put(key, new Item(key, nameAndSection[0], nameAndSection[1]));
        }
        return new Items(items);
    }

    private Joins loadJoins() {
        final Map<String, String> pairs = activity.getKeyValuePairsStartingWithPrefix(JOIN_PREFIX);
        final List<Join> js = new ArrayList<Join>(pairs.size());
        String[] joinInfo;
        String[] itemIds;
        for (String key : pairs.keySet()) {
            joinInfo = pairs.get(key).split(SEPARATOR);
            itemIds = joinInfo[1].split(LIST_SEPARATOR);
            js.add(new Join(key, joinInfo[0], items(itemIds), joinInfo[2]));
        }
        return new Joins(js);
    }

    private void loadDefaultSections() {
        loadHomeSection();
        loadHelpSection();
        loadInventorySection();
        loadEndSection();
        loadLoadSection();
        loadSaveSection();
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
        help.setTemporary(true);
        this.story.addSection(help);
    }

    private void loadInventorySection() {
        final Section inventory = new Section(Section.INVENTORY);
        this.story.addSection(inventory);
    }

    private void loadLoadSection() {
        final Section loadSection = new Section(Section.LOADING);
        List<String> text = new ArrayList<String>(1);
        text.add(msg(Messages.GAME_LOADED));
        loadSection.setText(text);
        loadSection.setTemporary(true);
        this.story.addSection(loadSection);
    }

    private void loadSaveSection() {
        final Section saveSection = new Section(Section.SAVING);
        List<String> text = new ArrayList<String>(1);
        text.add(msg(Messages.GAME_SAVED));
        saveSection.setText(text);
        saveSection.setTemporary(true);
        this.story.addSection(saveSection);
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
        unavailable.setTemporary(true);
        this.story.addSection(unavailable);
    }

    private List<Section> loadSections() {
        List<Section> sections = new ArrayList<Section>();
        final int sectionQty = Integer.valueOf(msg(SECTIONS)).intValue();
        final int startingSectionId = Integer.valueOf(msg(STARTING)).intValue();
        final List<String> endingSectionsIds = Arrays.asList(msg(ENDING).split(LIST_SEPARATOR));
        String id;
        Section section;
        for (int i = 1; i <= sectionQty; i++) {
            id = String.valueOf(i);
            section = new Section(id);
            if (i == startingSectionId)
                section.setStarting(true);
            if (endingSectionsIds.contains(id))
                section.setEnding(true);
            section.setText(loadSectionText(SECTION_PREFIX + id + "_"));
            section.setLinks(loadSectionLinks(section));
            section.setItemsGets(loadSectionItemGets(id));
            section.setItemDrops(loadSectionItemDrops(id));
            section.setNoteGets(loadSectionNoteGets(id));
            section.setNoteDrops(loadSectionNoteDrops(id));
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

    private String[] loadSectionNoteGets(final String id) {
        final String notesGroup = msg(SECTION_PREFIX + id + SECTION_NOTES_GET_SUFFIX); // es: s_5_notes_get
        if (notesGroup != null) {
            return notesGroup.split(LIST_SEPARATOR);
        }
        return new String[]{};
    }

    private String[] loadSectionNoteDrops(final String id) {
        final String notesGroup = msg(SECTION_PREFIX + id + SECTION_NOTES_DROP_SUFFIX); // es: s_5_notes_drop
        if (notesGroup != null) {
            return notesGroup.split(LIST_SEPARATOR);
        }
        return new String[]{};
    }

    private List<Link> loadSectionLinks(final Section section) {
        final Map<String, String> sectionLinks = activity.getKeyValuePairsStartingWithPrefix(SECTION_PREFIX + section.getId() + SECTION_LINK_SUFFIX);// es: s_4_link
        if (sectionLinks != null) {
            List<Link> links = new ArrayList<Link>(sectionLinks.size());
            String sectionLink;
            String[] linkInfo;
            Link link;
            int linkIdNumber = 0;
            List<String> orderedKeys = new ArrayList<String>(sectionLinks.keySet());
            Collections.sort(orderedKeys);
            for (String linkId : orderedKeys) {

                // es: s_6_link_3 --> 9:c_across,c_corridor:i_torch
                sectionLink = sectionLinks.get(linkId);
                linkInfo = sectionLink.split(SEPARATOR);
                linkIdNumber++;
                link = new Link(String.valueOf(linkIdNumber), section, linkInfo[0]); // s_6_link_3, 9

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
                // ex: s_5_switch_2 --> par:1:3:Davanti a te c\'è una porta aperta.
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
                            switchInfo[2],
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
        Section inventorySection = story.getSection(Section.INVENTORY);
        inventorySection.clearLinks();
        inventorySection.addLink("1", current.getId(), new String[]{Commands.GO_BACK}, null);
        final List<Item> items = this.character.getInventory().getItems();
        List<String> text = new ArrayList<String>(items.size() + 1);
        if (items.isEmpty())
            text.add(msg(Messages.EMPTY_INVENTORY));
        else {
            text.add(msg(Messages.INVENTORY));
            int linkId = 1;
            // per ogni oggetto, aggiungo il nome alla lista e un link "guardo"
            for (Item item : items) {
                text.add(item.getName());
                linkId++;
                inventorySection.addLink(String.valueOf(linkId), null, new String[]{Commands.EXAMINE}, new String[]{item.getId()});
            }
            // per ogni unione di oggetti disponibile, aggiungo un link "unisco"
            final List<Join> availableJoins = this.joins.getAvailableJoins(this.character.getInventory());
            for (final Join join : availableJoins) {
                linkId++;
                inventorySection.addLink(String.valueOf(linkId), join.getTargetSectionId(), new String[]{Commands.JOIN}, new String[]{join.getWords()});
            }
        }
        inventorySection.setText(text);
        return inventorySection;
    }

    Section createExamineSection(final Section current, final String input) {
        List<String> text = new ArrayList<String>(1);

        List<Item> items = this.character.getInventory().getItems();
        for (final Item item : items) {
            if (item.check(input)) {
                // come descrizione usa il paragrafo di testo della sezione che descrive l'oggetto
                text.add(loadSectionText(item.getSectionId()).get(0));
            }
        }

        if (text.isEmpty())
            text.add(msg(Messages.CANT_EXAMINE));

        return createTemporarySection(text);
    }

    Section createJoinSection(final String input) {
        final String joinResult = this.joins.getJoinResult(input, this.character.getInventory());
        Section joinSection;
        if (joinResult != null) {
            joinSection = this.story.getSection(joinResult);
            joinSection.setTemporary(true);
            return joinSection;
        }
        final List<String> text = new ArrayList<String>(1);
        text.add(msg(Messages.CANT_JOIN));
        return createTemporarySection(text);
    }

    Section createAvailableActionsSection(final Section current) {
        final List<Link> links = current.getLinks();
        List<String> text = new ArrayList<String>(links.size());
        String[] commandIds;
        for (final Link link : links) {
            commandIds = link.getCommandIds();
            for (final String commandId : commandIds) {
                text.add(command(commandId).getCommandWords());
            }
        }
        return createTemporarySection(text);
    }

    Section createAreYouSureSection(final Section yesResult, final Section noResult) {
        final Section section = new Section(Section.ARE_YOU_SURE);
        final List<String> text = new ArrayList<String>(1);
        text.add(msg(Messages.ARE_YOU_SURE));
        section.setText(text);
        section.addLink("1", yesResult.getId(), new String[]{Commands.YES}, null);
        section.addLink("2", noResult.getId(), new String[]{Commands.NO}, null);
        return section;
    }

    /**
     * @param text
     * @return sezione dotata di testo che torna sempre direttamente alla sezione attuale
     */
    private Section createTemporarySection(final List<String> text) {
        Section tempSection = new Section(Section.TEMPORARY);
        tempSection.setText(text);
        tempSection.setTemporary(true);
        return tempSection;
    }

    ///////// init, SAVE & LOAD

    public void init() {
        resetStory();
        this.story.home();
    }

    public void load(final String sectionId, final String inventoryItemIds, final String notesIds, final String paragraphSwitches, final String linkSwitches) {
        resetStory();
        story.setCurrent(story.getSection(sectionId));
        loadInventory(inventoryItemIds);
        loadNotes(notesIds);
        parseAndLoadSwitches(paragraphSwitches, linkSwitches);
        story.setPhase(StoryPhase.STARTED);
    }

    /**
     * Carica gli oggetti informati nell'inventario.
     *
     * @param inventoryItemIds ex: "i_key,i_torch,i_ring"
     */
    void loadInventory(final String inventoryItemIds) {
        this.character.getInventory().setItems(items(inventoryItemIds.split(StoryLoader.LIST_SEPARATOR)));
    }

    private void loadNotes(final String notesIds) {
        final String[] ids = notesIds.split(StoryLoader.LIST_SEPARATOR);
        for (final String id : ids) {
            this.character.getNotes().add(id, msg(id));
        }
    }

    /**
     * Crea oggetti ParagraphSwitch e LinkSwitch a partire dal formato String esemplificato qui
     * sotto, quindi li carica, applicando i loro effetti sulla storia, e li immagazzina in liste
     * locali.
     *
     * @param paragraphSwitches ex: "1:3:Davanti a te c\'è una porta aperta;2:2"
     *                          tradotto:
     *                          - sostituisci il terzo paragrafo della sezione 1 con il testo
     *                          "Davanti a te c'è una porta aperta";
     *                          - cancella il secondo paragrafo della sezione 2
     * @param linkSwitches      ex: "1:2;1:3;1:-1:6:c_go_on,c_straight_on,c_proceed"
     *                          tradotto:
     *                          - cancella il secondo link della sezione 1
     *                          - cancella il terzo link della sezione 1
     *                          - aggiungi un nuovo link alla sezione 1, che punti alla sezione 6
     *                          - cambia la nota di i_desk in n_9
     */
    void parseAndLoadSwitches(final String paragraphSwitches, final String linkSwitches) {

        if (!paragraphSwitches.isEmpty()) {
            String[] switchInfo;
            final String[] pss = paragraphSwitches.split(STRONG_SEPARATOR);
            final List<ParagraphSwitch> pars = new ArrayList<ParagraphSwitch>(pss.length);
            for (final String ps : pss) {
                switchInfo = ps.split(SEPARATOR);
                pars.add(new ParagraphSwitch(switchInfo[0], Integer.valueOf(switchInfo[1]).intValue(), switchInfo.length == 3 ? switchInfo[2] : ""));
            }
            loadParagraphSwitches(pars);
        }

        if (!linkSwitches.isEmpty()) {
            String[] switchInfo;
            final String[] lss = linkSwitches.split(STRONG_SEPARATOR);
            final List<LinkSwitch> links = new ArrayList<LinkSwitch>(lss.length);
            for (final String ls : lss) {
                switchInfo = ls.split(SEPARATOR);
                links.add(new LinkSwitch(switchInfo[0],
                        switchInfo[1],
                        switchInfo.length > 2 ? switchInfo[2] : "",
                        switchInfo.length > 3 ? switchInfo[3].split(LIST_SEPARATOR) : new String[]{},
                        switchInfo.length > 4 ? switchInfo[4].split(LIST_SEPARATOR) : new String[]{}));
            }
            loadLinkSwitches(links);
        }
    }

    /**
     * Carica i ParagraphSwitch informati, applicando i loro effetti sulla storia, quindi li
     * immagazzina in una lista locale.
     *
     * @param switches
     */
    void loadParagraphSwitches(final List<ParagraphSwitch> switches) {
        Section section;
        String newParagraph;
        int position;
        for (final ParagraphSwitch pSwitch : switches) {
            section = this.story.getSection(pSwitch.getSectionId());
            newParagraph = pSwitch.getNewParagraph();
            position = pSwitch.getParagraphPosition();
            if (newParagraph.isEmpty())
                section.removeParagraph(position);
            else if (position == -1)
                section.addParagraph(newParagraph);
            else
                section.updateParagraph(newParagraph, position);
        }
        this.paragraphSwitchesSoFar.addAll(switches);
    }

    /**
     * Carica i LinkSwitch informati, applicando i loro effetti sulla storia, quindi li
     * immagazzina in una lista locale.
     *
     * @param switches
     */
    void loadLinkSwitches(final List<LinkSwitch> switches) {
        Section section;
        String linkId;
        String nextSection;
        String[] commandIds;
        String[] itemIds;
        for (final LinkSwitch lSwitch : switches) {
            section = this.story.getSection(lSwitch.getSectionId());
            linkId = lSwitch.getLinkId();
            nextSection = lSwitch.getNextSection();
            commandIds = lSwitch.getCommandIds();
            itemIds = lSwitch.getItemIds();
            if (nextSection.isEmpty())
                section.removeLink(linkId);
            else if (section.getLink(linkId) == null)
                section.addLink(linkId, nextSection, commandIds, itemIds);
            else
                section.updateLink(linkId, nextSection, commandIds, itemIds);
        }
        this.linkSwitchesSoFar.addAll(switches);
    }

    /**
     * Trasforma il contenuto dell'inventario nel formato String esemplificato qui sotto.
     *
     * @return ex: "i_key,i_torch,i_ring"
     */
    public String stringifyInventory() {
        final StringBuilder sb = new StringBuilder();
        final List<Item> itemList = this.character.getInventory().getItems();
        if (!itemList.isEmpty()) {
            for (final Item item : itemList) {
                sb.append(item.getId()).append(StoryLoader.LIST_SEPARATOR);
            }
            sb.delete(sb.length() - StoryLoader.LIST_SEPARATOR.length(), sb.length());
        }
        return sb.toString();
    }

    /**
     * Trasforma le note del giocatore nel formato String esemplificato qui sotto.
     *
     * @return ex: "n_1,n_4,n_8"
     */
    public String stringifyNotes() {
        final StringBuilder sb = new StringBuilder();
        final Collection<String> notesIds = this.character.getNotes().getIds();
        if (!notesIds.isEmpty()) {
            for (final String noteId : notesIds) {
                sb.append(noteId).append(StoryLoader.LIST_SEPARATOR);
            }
            sb.delete(sb.length() - StoryLoader.LIST_SEPARATOR.length(), sb.length());
        }
        return sb.toString();
    }

    /**
     * Trasforma i ParagraphSwitch immagazzinati nel corso della storia nel formato String
     * esemplificato qui sotto.
     *
     * @return ex: "1:3:Davanti a te c\'è una porta aperta;2:2"
     * tradotto:
     * - sostituisci il terzo paragrafo della sezione 1 con il testo
     * "Davanti a te c'è una porta aperta";
     * - cancella il secondo paragrafo della sezione 2
     */
    public String stringifyParagraphSwitchesSoFar() {
        final StringBuilder sb = new StringBuilder();
        if (!this.paragraphSwitchesSoFar.isEmpty()) {
            for (final ParagraphSwitch ps : this.paragraphSwitchesSoFar) {
                sb.append(ps.getSectionId()).append(SEPARATOR);
                sb.append(ps.getParagraphPosition()).append(SEPARATOR);
                if (!ps.getNewParagraph().isEmpty())
                    sb.append(ps.getNewParagraph());
                sb.append(STRONG_SEPARATOR);
            }
            sb.delete(sb.length() - STRONG_SEPARATOR.length(), sb.length());
        }
        return sb.toString();
    }

    /**
     * Trasforma i LinkSwitch immagazzinati nel corso della storia nel formato String
     * esemplificato qui sotto.
     *
     * @return ex: "1:2;1:3;1:-1:6:c_go_on,c_straight_on,c_proceed"
     * tradotto:
     * - cancella il secondo link della sezione 1
     * - cancella il terzo link della sezione 1
     * - aggiungi un nuovo link alla sezione 1, che punti alla sezione 6
     * in base ai comandi c_go_on,c_straight_on,c_proceed
     */
    public String stringifyLinkSwitchesSoFar() {
        final StringBuilder sb = new StringBuilder();
        StringBuilder commands;
        StringBuilder items;
        if (!this.linkSwitchesSoFar.isEmpty()) {
            for (final LinkSwitch ls : this.linkSwitchesSoFar) {
                sb.append(ls.getSectionId()).append(SEPARATOR);
                sb.append(ls.getLinkId());
                if (!ls.getNextSection().isEmpty()) {
                    sb.append(SEPARATOR).append(ls.getNextSection());

                    if (ls.getCommandIds() != null && ls.getCommandIds().length > 0) {
                        commands = new StringBuilder();
                        for (final String commandId : ls.getCommandIds()) {
                            commands.append(commandId).append(LIST_SEPARATOR);
                        }
                        commands.delete(commands.length() - LIST_SEPARATOR.length(), commands.length());
                        sb.append(SEPARATOR).append(commands.toString());

                        if (ls.getItemIds() != null && ls.getItemIds().length > 0) {
                            items = new StringBuilder();
                            for (final String itemId : ls.getItemIds()) {
                                items.append(itemId).append(LIST_SEPARATOR);
                            }
                            items.delete(items.length() - LIST_SEPARATOR.length(), items.length());
                            sb.append(SEPARATOR).append(items.toString());
                        }
                    }
                }
                sb.append(STRONG_SEPARATOR);
            }
            sb.delete(sb.length() - STRONG_SEPARATOR.length(), sb.length());
        }
        return sb.toString();
    }

    ///////// NOTES

    public Section createNotesSection() {
        List<String> text = new ArrayList<String>(1);
        final Collection<String> notes = this.character.getNotes().get();
        if (!notes.isEmpty())
            for (final String note : notes)
                text.add(note);
        else
            text.add(msg(Messages.EMPTY_NOTES));
        return createTemporarySection(text);
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

    public List<String> getDefaultCommands() {
        return this.commands.getDefaultCommands(this.story.getPhase());
    }
}
