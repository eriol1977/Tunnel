package com.example.francesco.tunnel.story;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Francesco on 18/11/2014.
 */
public class Story {

    private final StoryItems items;

    private Section starting;

    private Section current;

    private final Map<String, Section> sections = new HashMap<String, Section>();

    private final Map<String, List<Link>> outcomes = new HashMap<String, List<Link>>();

    private boolean started = false;

    private boolean ended = false;

    private boolean suspended = false; // per esempio, nel menu di aiuto o nell'inventario

    public static final String HOME = "home";
    public static final String HELP = "help";
    public static final String END = "end";
    public static final String QUIT = "quit";

    public Story(final StoryItems items) {
        this.items = items;
    }

    void addSection(final Section section) {
        this.sections.put(section.getId(), section);
        this.outcomes.put(section.getId(), new ArrayList<Link>());
    }

    void addOutcome(final String from, final String to, final String outcome) {
        final List<Link> sectionOutcomes = this.outcomes.get(from);
        sectionOutcomes.add(new Link(outcome, this.sections.get(to)));
    }

    void addDirectOutcome(final String from, final String to) {
        this.sections.get(from).setOneOutcome(true);
        final List<Link> sectionOutcomes = this.outcomes.get(from);
        sectionOutcomes.clear();
        sectionOutcomes.add(new Link(null, this.sections.get(to)));
    }

    void introduce() {
        setCurrent(this.sections.get(HOME));
    }

    void start() throws StoryException {
        if(starting == null)
            throw new StoryException();
        setCurrent(starting);
        started = true;
        ended = false;
    }

    void proceed() {
        Link outcome = this.outcomes.get(this.current.getId()).get(0);
        setCurrent(outcome.getNextSection());
    }

    void proceed(final String outcome) throws StoryException {
        final List<Link> sectionOutcomes = this.outcomes.get(this.current.getId());
        boolean found = false;
        for (final Link o : sectionOutcomes) {
            if (o.getOutcome().equals(outcome)) {
                setCurrent(o.getNextSection());
                found = true;
                break;
            }
        }
        if (!found)
            throw new StoryException();
    }

    public void proceedToHelp() {
        setCurrent(this.sections.get(HELP));
    }

    void proceedToEnd() {

        setCurrent(this.sections.get(END));
    }

    void proceedToQuit() {
        setCurrent(this.sections.get(QUIT));
    }

    List<String> getOutcomes(final String id) {
        final List<Link> sectionOutcomes = this.outcomes.get(id);
        if(sectionOutcomes == null)
            return new ArrayList<String>();
        final List<String> result =  new ArrayList<String>(sectionOutcomes.size());
        for(final Link o: sectionOutcomes) {
            result.add(o.getOutcome());
        }
        return result;
    }

    boolean hasOneOutcome() {
        return getCurrent().hasOneOutcome();
    }

    void setStarting(Section starting) {
        this.starting = starting;
    }

    Section getCurrent() {
        return current;
    }

    void setCurrent(Section current) {
        this.current = current;
        if (current.isEnding()) {
            this.ended = true;
            this.started = false;
        }
    }

    public boolean isStarted() {
        return started;
    }

    boolean isEnded() {
        return ended;
    }

    public StoryItems getItems() {
        return items;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }
}