package com.example.francesco.tunnel.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.example.francesco.tunnel.R;
import com.example.francesco.tunnel.story.Story;
import com.example.francesco.tunnel.story.StoryMaker;
import com.example.francesco.tunnel.story.StoryTeller;
import com.example.francesco.tunnel.story.StringLoader;

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
 * - implementare comandi inventario, oggetti, prendi, usa, esamina
 * - testare storiella in inglese/portoghese
 * - rendere uniforme lo StoryTeller: usare stash() o fare come inventario per le sezioni extra?
 */

public abstract class StoryTellerActivity extends Activity implements View.OnClickListener {

    private String title;

    protected StringLoader sl;

    protected StoryTeller teller;

    protected StoryMaker maker;

    //private static final String STORY_DATA = "STORY_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sl = new StringLoader(this);

        maker = new StoryMaker(sl);

        //if (savedInstanceState != null) {
        //    teller = (StoryTeller) savedInstanceState.getSerializable(STORY_DATA);
        //} else {
        teller = new StoryTeller(buildStory(), sl);
        teller.introduce();
        //}
    }

    protected abstract Story buildStory();

    protected abstract void displayText(List<String> text);

    protected abstract void processInput();

    @Override
    public void onClick(View v) {

        if (teller.hasToQuit())
            finish();

        if (!teller.storyHasEnded()) {
            if (teller.hasOneOutcome()) {
                teller.proceed();
                displayText(teller.getCurrentText());
            } else {
                processInput();
            }
        } else {
            if (teller.isEndPhase()) {
                processInput();
            } else {
                teller.proceedToEnd();
                displayText(teller.getCurrentText());
            }
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