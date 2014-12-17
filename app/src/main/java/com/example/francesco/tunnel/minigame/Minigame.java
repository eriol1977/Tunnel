package com.example.francesco.tunnel.minigame;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Francesco on 17/12/2014.
 */
public class Minigame {

    public final static String PARAM_WIN_NEXT_SECTION = "winNextSection";

    public final static String PARAM_LOSE_NEXT_SECTION = "loseNextSection";

    public final static String RESULT_NEXT_SECTION = "nextSection";

    private final String activityClass;

    private final Map<String, String> parameters = new HashMap<>();

    public Minigame(final String activityClass, final String winNextSection, final String loseNextSection) {
        this.activityClass = activityClass;
        addParameter(PARAM_WIN_NEXT_SECTION, winNextSection);
        addParameter(PARAM_LOSE_NEXT_SECTION, loseNextSection);
    }

    public void addParameter(final String key, final String value) {
        this.parameters.put(key, value);
    }

    public String getParameter(final String key) {
        return this.parameters.get(key);
    }

    public String getActivityClass() {
        return activityClass;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
