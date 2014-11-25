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

    private final Map<String, Section> sections = new HashMap<String, Section>();

    void addSection(final Section section) {
        this.sections.put(section.getId(), section);
        if (section.isStarting())
            this.starting = section;
    }

    public boolean hasDirectOutcome() {
        return this.current.hasDirectOutcome();
    }

    public void home() {
        setPhase(StoryPhase.HOME);
        proceedToHome();
    }

    void start() {
        setCurrent(starting);
        setPhase(StoryPhase.STARTED);
    }

    void setCurrent(final Section section) {
        this.current = section;
        this.current.activate();
        if (this.current.isEnding()) {
            setPhase(StoryPhase.ENDED);
        }
    }

    public void proceed() {
        if (this.phase.equals(StoryPhase.ENDED))
            proceedToEnd();
        else {
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
                    stash();
                    // TODO
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
                }
                break;
        }

        return linkFound;
    }

    private void loadGame() {
        // TODO
    }

    private void saveGame() {
        stash();
        // TODO
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

    private void proceedToEnd() {
        setCurrent(getSection(Section.END_SECTION));
    }

    private void proceedToQuit() {
        setCurrent(getSection(Section.QUIT_SECTION));
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
        setPhase(StoryPhase.QUIT);
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

    private void setPhase(StoryPhase phase) {
        this.phase = phase;
    }

    private Command command(final String key) {
        return StoryLoader.getInstance().command(key);
    }

    public List<String> getCurrentText() {
        return this.current.getText();
    }
}