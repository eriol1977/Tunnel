package com.example.francesco.tunnel.story;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Francesco on 18/11/2014.
 */
public class Story {

    private StoryPhase phase;

    private Section starting;

    private Section current;

    private Section stashed;

    private final Character character;

    private Map<String, Section> sections = new HashMap<String, Section>();

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

    void home() {
        setPhase(StoryPhase.HOME);
        proceedToHome();
    }

    void reset() {
        this.sections.clear();
        this.phase = null;
        this.starting = null;
        this.stashed = null;
        this.current = null;
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
        } else if (section.getId().equals(this.starting.getId())) {
            StoryLoader.getInstance().resetStory();
            setPhase(StoryPhase.STARTED);
        } else if (section.getId().equals(Section.LOADING)) {
            setPhase(StoryPhase.LOADING);
        } else if (section.getId().equals(Section.SAVING)) {
            setPhase(StoryPhase.SAVING);
        }
        this.current = section;
        this.current.activate();
    }

    public void proceed() {
        if (this.phase.equals(StoryPhase.ENDED))
            proceedToEnd();
        else {
            // dopo aver salvato, torna allo stato normale
            if (this.phase.equals(StoryPhase.SAVING))
                setPhase(StoryPhase.STARTED);

            if (this.current.isTemporary())
                restore();
            else {
                final String nextSectionId = this.current.getLink(0).getNextSection();
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
                    proceedToExamine(input);
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
                } else if (command(Commands.JOIN).check(input)) {
                    proceedToJoin(input);
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
        stash();
        final Section loadSection = StoryLoader.getInstance().createLoadSection(this.current);
        if (this.phase.equals(StoryPhase.HOME))
            setCurrent(loadSection);
        else // prima di caricare un'altra partita, chiede conferma
            setCurrent(StoryLoader.getInstance().createAreYouSureSection(loadSection, this.current));
    }

    private void saveGame() {
        stash();
        final Section saveSection = StoryLoader.getInstance().createSaveSection(this.current);
        // prima di sovrascrivere il salvataggio, chiede conferma
        setCurrent(StoryLoader.getInstance().createAreYouSureSection(saveSection, this.current));
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
        setCurrent(StoryLoader.getInstance().createNotesSection(this.current));
    }

    private void proceedToAvailableActions() {
        stash();
        setCurrent(StoryLoader.getInstance().createAvailableActionsSection(this.current));
    }

    private void proceedToExamine(final String input) {
        stash();
        setCurrent(StoryLoader.getInstance().createExamineSection(this.current, input));
    }

    private void proceedToJoin(final String input) {
        stash();
        setCurrent(StoryLoader.getInstance().createJoinSection(this.current, input));
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
        this.stashed = this.current;
    }

    private void restore() {
        setCurrent(this.stashed);
    }

    Section getSection(final String id) {
        return this.sections.get(id);
    }

    public StoryPhase getPhase() {
        return phase;
    }

    void setPhase(StoryPhase phase) {
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
            id = this.stashed.getId();
        return id;
    }
}