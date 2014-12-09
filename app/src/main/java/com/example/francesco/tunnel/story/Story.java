package com.example.francesco.tunnel.story;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Francesco on 18/11/2014.
 */
public class Story {

    private StoryPhase phase;

    private Section starting;

    private Section current;

    private LinkedList<Section> stashed = new LinkedList<Section>();

    private final Character character;

    private Map<String, Section> sections = new HashMap<String, Section>();

    private boolean restartCommandIssued = false;

    public Story(Character character) {
        this.character = character;
    }

    void addSection(final Section section) {
        this.sections.put(section.getId(), section);
        if (section.isStarting())
            this.starting = section;
    }

    void setSections(final List<Section> sections) {
        for (final Section section : sections) {
            addSection(section);
        }
    }

    public boolean hasDirectOutcome() {
        return this.current.hasDirectOutcome();
    }

    public boolean unavailableCommand() {
        return this.current.getId().equals(Section.UNAVAILABLE_SECTION);
    }

    public boolean isTemporarySection() {
        return this.current.isTemporary();
    }

    /**
     * @return Tutti i possibili input di comando, in base alla sezione attuale.
     * ex: "Torno indietro","Apro porta","Prendo medaglione sole"
     */
    public List<String> getCommandInputs() {
        final List<Link> links = current.getLinks();
        List<String> text = new ArrayList<String>(links.size());
        String[] commandIds;
        String[] itemIds;
        String commandText;
        Item item = null;
        // comandi ricavati dai link definiti esplicitamente
        for (final Link link : links) {
            commandIds = link.getCommandIds();
            itemIds = link.getItemIds();
            if (commandIds.length > 0) { // è 0 con noItems, per non ripetere due volte lo stesso comando
                if (commandIds[0].equals(Commands.GET) || commandIds[0].equals(Commands.USE) || commandIds[0].equals(Commands.EXAMINE) || commandIds[0].equals(Commands.JOIN)) {
                    if (itemIds != null && itemIds.length > 0) {
                        if (commandIds[0].equals(Commands.JOIN)) {
                            commandText = command(commandIds[0]).getCommandWords() + " " + itemIds[0]; // in questo caso nel link c'è una parola (ex: "medaglioni") invece di un id
                            text.add(commandText);
                        } else {
                            item = StoryLoader.getInstance().item(itemIds[0]);
                            if (commandIds[0].equals(Commands.GET) || this.character.hasItem(item)) {
                                commandText = command(commandIds[0]).getCommandWords() + " " + item.getName();
                                text.add(commandText);
                            }
                        }
                    }
                } else {
                    commandText = command(commandIds[0]).getCommandWords();
                    text.add(commandText);
                }
            }
        }
        // comandi ricavati dalla presenza di "observables"
        List<Item> items = current.getObservableItems();
        if (items != null) {
            for (final Item i : items) {
                commandText = command(Commands.EXAMINE).getCommandWords() + " " + i.getName();
                text.add(commandText);
            }
        }
        // comandi ricavati dalla presenza di "usables"
        items = current.getUsableItems();
        if (items != null) {
            for (final Item i : items) {
                commandText = command(Commands.USE).getCommandWords() + " " + i.getName();
                text.add(commandText);
            }
        }
        return text;
    }

    void home() {
        setPhase(StoryPhase.HOME);
        proceedToHome();
    }

    void reset() {
        this.sections.clear();
        this.phase = null;
        this.starting = null;
        this.stashed.clear();
        this.current = null;
        this.restartCommandIssued = true;
    }

    void start() {
        if (this.phase.equals(StoryPhase.HOME))
            setCurrent(starting);
        else // chiede conferma prima di reiniziare la storia
            setCurrent(StoryLoader.getInstance().createAreYouSureSection(this.starting, this.current));
    }

    void setCurrent(final Section section) {
        if (section.isEnding()) {
            setPhase(StoryPhase.ENDED);
        } else if (section.getId().equals(Section.QUIT_SECTION)) {
            setPhase(StoryPhase.QUIT);
        } else if (restartCommandIssued && section.getId().equals(this.starting.getId())) {
            StoryLoader.getInstance().resetStory();
            setPhase(StoryPhase.STARTED);
            restartCommandIssued = false;
        } else if (section.getId().equals(Section.LOADING)) {
            setPhase(StoryPhase.LOADING);
        } else if (section.getId().equals(Section.SAVING)) {
            setPhase(StoryPhase.SAVING);
        } else if (section.getId().equals(Section.INVENTORY)) {
            setPhase(StoryPhase.INVENTORY);
        }
        this.current = section;
        this.current.activate();
    }

    public void proceed() {
        if (this.phase.equals(StoryPhase.INVENTORY))
            setPhase(StoryPhase.STARTED);

        if (this.phase.equals(StoryPhase.ENDED))
            proceedToEnd();
        else {
            if (this.current.isTemporary())
                restore();
            else {
                final String nextSectionId = this.current.getFirstLink().getNextSection();
                setCurrent(getSection(nextSectionId));
            }
        }
    }

    public void proceed(final String input) {

        boolean linkFound = proceedToDefaultLink(input);

        if (!linkFound)
            linkFound = proceedToLink(input);

        if (!linkFound)
            proceedToUnavailable();
    }

    private boolean proceedToDefaultLink(final String input) {
        boolean linkFound = false;

        switch (this.phase) {
            case HOME:
                if (command(Commands.START).check(input) || command(Commands.NEW_GAME).check(input)) {
                    start();
                    linkFound = true;
                } else if (command(Commands.INSTRUCTIONS).check(input) || command(Commands.HELP).check(input)) {
                    proceedToHelp();
                    linkFound = true;
                } else if (command(Commands.LOAD_GAME).check(input)) {
                    loadGame();
                    linkFound = true;
                } else if (command(Commands.QUIT).check(input)) {
                    quit();
                    linkFound = true;
                } else if (command(Commands.REPEAT).check(input)) {
                    linkFound = true;
                }
                break;
            case STARTED:
                if (command(Commands.REPEAT).check(input)) {
                    linkFound = true;
                } else if (command(Commands.INSTRUCTIONS).check(input) || command(Commands.HELP).check(input)) {
                    proceedToHelp();
                    linkFound = true;
                } else if (command(Commands.INVENTORY).check(input)) {
                    proceedToInventory();
                    linkFound = true;
                } else if (command(Commands.EXAMINE).check(input)) {
                    proceedToExamine(input, false);
                    linkFound = true;
                } else if (command(Commands.LOAD_GAME).check(input)) {
                    loadGame();
                    linkFound = true;
                } else if (command(Commands.SAVE_GAME).check(input)) {
                    saveGame();
                    linkFound = true;
                } else if (command(Commands.QUIT).check(input)) {
                    quit();
                    linkFound = true;
                } else if (command(Commands.START).check(input) || command(Commands.NEW_GAME).check(input)) {
                    start();
                    linkFound = true;
                } else if (command(Commands.ACTIONS).check(input) || command(Commands.COMMANDS).check(input)) {
                    proceedToAvailableActions();
                    linkFound = true;
                } else if (command(Commands.NOTES).check(input)) {
                    proceedToNotes();
                    linkFound = true;
                }
                break;
            case INVENTORY:
                if (command(Commands.EXAMINE).check(input)) {
                    proceedToExamine(input, true);
                    linkFound = true;
                } else if (command(Commands.JOIN).check(input)) {
                    proceedToJoin(input);
                    linkFound = true;
                } else if (command(Commands.GO_BACK).check(input)) {
                    setPhase(StoryPhase.STARTED);
                    restore();
                    linkFound = true;
                }
                break;
            case ENDED:
                if (command(Commands.QUIT).check(input)) {
                    quit();
                    linkFound = true;
                } else if (command(Commands.START).check(input) || command(Commands.NEW_GAME).check(input)) {
                    start();
                    linkFound = true;
                } else if (command(Commands.LOAD_GAME).check(input)) {
                    loadGame();
                    linkFound = true;
                } else if (command(Commands.INSTRUCTIONS).check(input) || command(Commands.HELP).check(input)) {
                    proceedToHelp();
                    linkFound = true;
                } else if (command(Commands.REPEAT).check(input)) {
                    linkFound = true;
                }
                break;
        }

        return linkFound;
    }

    private void loadGame() {
        setCurrent(getSection(Section.LOADING));
    }

    private void saveGame() {
        stash();
        setCurrent(getSection(Section.SAVING));
    }

    private void proceedToHome() {
        setCurrent(getSection(Section.HOME_SECTION));
    }

    private void proceedToHelp() {
        stash();
        setCurrent(getSection(Section.HELP_SECTION));
    }

    private void proceedToInventory() {
        stash();
        setCurrent(StoryLoader.getInstance().createInventorySection(this.current));
    }

    private void proceedToNotes() {
        stash();
        setCurrent(StoryLoader.getInstance().createNotesSection());
    }

    private void proceedToAvailableActions() {
        stash();
        setCurrent(StoryLoader.getInstance().createAvailableActionsSection(this.current));
    }

    private void proceedToExamine(final String input, final boolean inInventory) {
        stash();
        setCurrent(StoryLoader.getInstance().createExamineSection(this.current, input, inInventory));
    }

    private void proceedToJoin(final String input) {
        stash();
        setCurrent(StoryLoader.getInstance().createJoinSection(input));
    }

    private void proceedToEnd() {
        setCurrent(getSection(Section.END_SECTION));
    }

    public void proceedToQuit() {
        setCurrent(StoryLoader.getInstance().createAreYouSureSection(getSection(Section.QUIT_SECTION), this.current));
    }

    private boolean proceedToLink(final String input) {
        boolean linkFound = false;
        String nextSectionId;
        final List<Link> links = this.current.getLinks();
        for (final Link link : links) {
            if (link.check(input)) {
                nextSectionId = link.getNextSection();
                setCurrent(getSection(nextSectionId));
                linkFound = true;
                break;
            }
        }
        return linkFound;
    }

    private void proceedToUnavailable() {
        stash();
        setCurrent(getSection(Section.UNAVAILABLE_SECTION));
    }

    void quit() {
        proceedToQuit();
    }

    private void stash() {
        this.stashed.add(this.current);
    }

    private Section restore() {
        final Section last = this.stashed.removeLast();
        if (last.getId().equals(Section.INVENTORY))
            // quando torno all'inventario da un'altra sezione, per esempio "unisco", l'inventario potrebbe
            // essere cambiato, per cui devo aggiornare la sezione
            setCurrent(StoryLoader.getInstance().createInventorySection(getSection(last.getFirstLink().getNextSection())));
        else
            setCurrent(last);
        return this.current;
    }

    Section getSection(final String id) {
        return this.sections.get(id);
    }

    public StoryPhase getPhase() {
        return phase;
    }

    public void setPhase(StoryPhase phase) {
        this.phase = phase;
    }

    private Command command(final String key) {
        return StoryLoader.getInstance().command(key);
    }

    public List<String> getCurrentText() {
        return this.current.getText();
    }

    /**
     * @param temporary
     * @return In caso di salvataggio temporario, dovuto all'interruzione dell'app, ritorna l'id della
     * sezione attuale; altrimenti, siccome ci troviamo nella sezione "salva", ritorna l'id della
     * sezione immagazzinata, che sarebbe l'attuale sezione narrativa
     */
    public String getSavingSectionId(final boolean temporary) {
        String id = null;
        if (temporary && this.current != null)
            id = this.current.getId();
        else
            id = restore().getId();
        return id;
    }
}