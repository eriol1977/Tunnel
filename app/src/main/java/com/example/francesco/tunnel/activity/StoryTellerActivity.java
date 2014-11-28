package com.example.francesco.tunnel.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.example.francesco.tunnel.R;
import com.example.francesco.tunnel.story.Story;
import com.example.francesco.tunnel.story.StoryLoader;
import com.example.francesco.tunnel.story.StoryPhase;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODOS:
 * - immagazzinare i dati quando si interrompe la partita per un po' (tipo home screen, o telefonata ricevuta...)
 * [dopo un back, ricomincia da capo; dopo un home, resta senza voce]
 * - implementare salva e carica partita
 * - commentare codice
 * - implementare comando "comandi"
 * - testare storiella in inglese/portoghese
 */

public abstract class StoryTellerActivity extends Activity implements View.OnClickListener {

    private String title;

    protected StoryLoader loader;

    protected Story story;

    //private static final String STORY_DATA = "STORY_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loader = StoryLoader.getInstance(this);
        story = loader.getStory();

        //if (savedInstanceState != null) {
        //    teller = (StoryTeller) savedInstanceState.getSerializable(STORY_DATA);
        //} else {
        story.home();
        //}
    }

    protected abstract void displayText(List<String> text);

    protected abstract void processInput();

    @Override
    public void onClick(View v) {

        if (story.getPhase().equals(StoryPhase.QUIT))
            finish();

        if (story.hasDirectOutcome()) {
            if (story.unavailableCommand()) {
                story.proceed();
                processInput();
            } else {
                story.proceed();
                displayText(story.getCurrentText());
            }
        } else {
            processInput();
        }
    }

    //@Override
    //protected void onSaveInstanceState(Bundle outState) {
    //    outState.putSerializable(STORY_DATA, teller);
    //    super.onSaveInstanceState(outState);
    //}

    protected void setTitle(String title) {
        this.title = title;
    }

    public String s(final String key) {
        try {
            int resId = getResources().getIdentifier(key, "string", getPackageName());
            return getResources().getString(resId);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, String> getKeyValuePairsStartingWithPrefix(final String prefix) {
        Map<String, String> result = new HashMap<String, String>();
        for (Field field : R.string.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && !Modifier.isPrivate(field.getModifiers()) && field.getType().equals(int.class)) {
                try {
                    if (field.getName().startsWith(prefix)) {
                        result.put(field.getName(), getResources().getString(field.getInt(null)));
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        return result;
    }
}