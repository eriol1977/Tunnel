package com.example.francesco.tunnel.story;

import com.example.francesco.tunnel.character.Character;

import java.util.List;

/**
 * Created by Francesco on 18/11/2014.
 */
public class StoryTeller {

    private final com.example.francesco.tunnel.character.Character character;

    private final Story story;

    private final StringLoader sl;

    private Section stashed;

    private boolean endPhase = false;

    private boolean quit = false;

    public StoryTeller(final Story story, final StringLoader sl) {
        this.character = new Character();
        this.story = story;
        this.sl = sl;
    }

    public void introduce() {
        this.story.introduce();
    }

    public void start() throws StoryException {
        this.endPhase = false;
        this.story.start();
    }

    public List<String> getCurrentText() {
        return this.story.getCurrent().getText();
    }

    public void proceed() {
        this.story.proceed();
    }

    public void proceed(final String outcome) throws StoryException {

        if (outcome.equalsIgnoreCase(sl.REPEAT))
            return;

        if (!this.story.isSuspended() && (outcome.equalsIgnoreCase(sl.INSTRUCTIONS) || outcome.equalsIgnoreCase(sl.HELP))) {
            stash();
            this.story.proceedToHelp();
            return;
        }

        if (outcome.equalsIgnoreCase(sl.BACK_TO_GAME) && stashed != null) {
            restore();
            return;
        }

        if (outcome.contains(sl.START) || outcome.contains(sl.NEW) || outcome.contains(sl.GAME)) {
            start();
            return;
        }

        if (this.story.isStarted() && !this.story.isSuspended() && outcome.equalsIgnoreCase(sl.INVENTORY)) {
            this.story.setCurrent(new StoryMaker(sl).createInventorySection(character.getInventory(),this.story.getCurrent()));
            return;
        }

        if (this.story.isStarted() && !this.story.isSuspended() && outcome.equalsIgnoreCase(sl.ITEMS)) {
            this.story.setCurrent(new StoryMaker(sl).createItemsSection(this.story.getCurrent()));
            return;
        }

        if (this.story.isStarted() && !this.story.isSuspended() && outcome.contains(sl.GET)) {
            this.story.setCurrent(new StoryMaker(sl).createGetSection(this.story.getCurrent(),outcome,character.getInventory()));
            return;
        }

        if (this.story.isStarted() && outcome.equalsIgnoreCase(sl.SAVE_GAME)) {
            // TODO salvare partita
            return;
        }

        if (outcome.equalsIgnoreCase(sl.LOAD_GAME)) {
            // TODO caricare partita
            return;
        }

        if (outcome.equalsIgnoreCase(sl.QUIT)) {
            this.story.proceedToQuit();
            quit = true;
            return;
        }

        String[] outcomeKeywords = outcome.split("\\s+");
        String[] candidateKeywords;
        List<String> availableOutcomes = getCurrentOutcomes();
        String selectedOutcome = null;
        for (String candidate : availableOutcomes) {
            candidateKeywords = candidate.split("\\s+");
            for (String outcomeKeyword : outcomeKeywords) {
                for (String candidateKeyword : candidateKeywords) {
                    if (outcomeKeyword.equalsIgnoreCase(candidateKeyword)) {
                        selectedOutcome = candidate;
                        break;
                    }
                }
                if (selectedOutcome != null) {
                    break;
                }
            }
            if (selectedOutcome != null) {
                break;
            }
        }
        if (selectedOutcome != null) {
            this.story.proceed(selectedOutcome);
        } else {
            throw new StoryException();
        }
    }

    public void proceedToEnd() {
        this.endPhase = true;
        this.story.proceedToEnd();
    }

    public boolean hasOneOutcome() {
        return this.story.hasOneOutcome();
    }

    private List<String> getCurrentOutcomes() {
        return this.story.getOutcomes(this.story.getCurrent().getId());
    }

    public boolean storyHasEnded() {
        return this.story.isEnded();
    }

    public boolean isEndPhase() {
        return endPhase;
    }

    private void stash() {
        stashed = this.story.getCurrent();
        this.story.setSuspended(true);
    }

    private void restore() {
        this.story.setCurrent(stashed);
        this.story.setSuspended(false);
        stashed = null;
    }

    public boolean hasToQuit() {
        return quit;
    }
}
