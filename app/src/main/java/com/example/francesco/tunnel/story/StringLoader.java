package com.example.francesco.tunnel.story;

import com.example.francesco.tunnel.activity.StoryTellerActivity;

import java.util.Map;

/**
 * Created by Francesco on 20/11/2014.
 */
public class StringLoader {

    private final StoryTellerActivity activity;

    public final String GO_BACK;
    public final String ENTER;
    public final String RIGHT;
    public final String LEFT;
    public final String GO_STRAIGHT_ON;
    public final String GO_ON;
    public final String REPEAT;
    public final String INSTRUCTIONS;
    public final String HELP;
    public final String BACK_TO_GAME;
    public final String START;
    public final String NEW;
    public final String GAME;
    public final String SAVE_GAME;
    public final String LOAD_GAME;
    public final String QUIT;
    public final String UNAVAILABLE;
    public final String INVENTORY;
    public final String INVENTORY_CONTENT;
    public final String EMPTY_INVENTORY;
    public final String ITEMS;
    public final String SECTION_ITEMS;
    public final String EMPTY_SECTION_ITEMS;
    public final String GET;
    public final String NO_OBJECT;

    public StringLoader(final StoryTellerActivity activity) {

        this.activity = activity;

        REPEAT = activity.s(CommandIds.REPEAT);
        INSTRUCTIONS = activity.s(CommandIds.INSTRUCTIONS);
        HELP = activity.s(CommandIds.HELP);
        BACK_TO_GAME = activity.s(CommandIds.BACK_TO_GAME);
        START = activity.s(CommandIds.START);
        NEW = activity.s(CommandIds.NEW);
        GAME = activity.s(CommandIds.GAME);
        SAVE_GAME = activity.s(CommandIds.SAVE_GAME);
        LOAD_GAME = activity.s(CommandIds.LOAD_GAME);
        QUIT = activity.s(CommandIds.QUIT);
        GO_BACK = activity.s(CommandIds.GO_BACK);
        ENTER = activity.s(CommandIds.ENTER);
        RIGHT = activity.s(CommandIds.RIGHT);
        LEFT = activity.s(CommandIds.LEFT);
        GO_STRAIGHT_ON = activity.s(CommandIds.GO_STRAIGHT_ON);
        GO_ON = activity.s(CommandIds.GO_ON);
        INVENTORY = activity.s(CommandIds.INVENTORY);
        ITEMS = activity.s(CommandIds.ITEMS);
        GET = activity.s(CommandIds.GET);

        UNAVAILABLE = activity.s(MessageIds.UNAVAILABLE);
        INVENTORY_CONTENT = activity.s(MessageIds.INVENTORY);
        EMPTY_INVENTORY = activity.s(MessageIds.EMPTY_INVENTORY);
        SECTION_ITEMS = activity.s(MessageIds.SECTION_ITEMS);
        EMPTY_SECTION_ITEMS = activity.s(MessageIds.EMPTY_SECTION_ITEMS);
        NO_OBJECT = activity.s(MessageIds.NO_OBJECT);
    }

    public String s(final String key)
    {
        return this.activity.s(key);
    }

    public Map<String, String> getKeyValuePairsStartingWithPrefix(final String prefix) {
        return this.activity.getKeyValuePairsStartingWithPrefix(prefix);
    }
}
