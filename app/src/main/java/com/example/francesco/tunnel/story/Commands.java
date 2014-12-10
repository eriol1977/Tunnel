package com.example.francesco.tunnel.story;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Francesco on 24/11/2014.
 */
public class Commands {

    private final Map<String, Command> commands;

    /**
     * Default commands for all games
     */
    public final static String GET = "c_get";

    public final static String USE = "c_use";

    public final static String REPEAT = "c_repeat";

    public final static String INSTRUCTIONS = "c_instructions";

    public final static String HELP = "c_help";

    public final static String START = "c_start";

    public final static String NEW_GAME = "c_new_game";

    public final static String SAVE_GAME = "c_save_game";

    public final static String LOAD_GAME = "c_load_game";

    public final static String QUIT = "c_quit";

    public final static String INVENTORY = "c_inventory";

    public final static String EXAMINE = "c_examine";

    public final static String OBSERVE = "c_observe";

    public final static String GO_BACK = "c_go_back";

    public final static String ACTIONS = "c_actions";

    public final static String COMMANDS = "c_commands";

    public final static String JOIN = "c_join";

    public final static String YES = "c_yes";

    public final static String NO = "c_no";

    public Commands(Map<String, Command> commands) {
        this.commands = commands;
    }

    public Command get(final String id) {
        return commands.get(id);
    }

    public List<Command> get(final String... ids) {
        List<Command> found = new ArrayList<Command>();
        Command command;
        for (String id : ids) {
            command = get(id);
            if (command != null)
                found.add(command);
        }
        return found;
    }

    public List<String> getDefaultCommands(final StoryPhase storyPhase) {
        List<String> commands = new ArrayList<String>();
        if (storyPhase.equals(StoryPhase.HOME) || storyPhase.equals(StoryPhase.ENDED)) {
            commands.add(get(NEW_GAME).getCommandWords());
            commands.add(get(LOAD_GAME).getCommandWords());
            commands.add(get(INSTRUCTIONS).getCommandWords());
            commands.add(get(QUIT).getCommandWords());
            commands.add(get(REPEAT).getCommandWords());
        } else if (storyPhase.equals(StoryPhase.STARTED)) {
            commands.add(get(INVENTORY).getCommandWords());
            commands.add(get(SAVE_GAME).getCommandWords());
            commands.add(get(NEW_GAME).getCommandWords());
            commands.add(get(LOAD_GAME).getCommandWords());
            commands.add(get(INSTRUCTIONS).getCommandWords());
            commands.add(get(QUIT).getCommandWords());
            commands.add(get(REPEAT).getCommandWords());
        }
        return commands;
    }
}
