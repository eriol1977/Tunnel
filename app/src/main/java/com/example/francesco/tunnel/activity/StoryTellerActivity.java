package com.example.francesco.tunnel.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.francesco.tunnel.R;
import com.example.francesco.tunnel.story.Section;
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
 * - commentare codice
 * - testare storiella in inglese/portoghese
 */

public abstract class StoryTellerActivity extends Activity implements View.OnClickListener {

    private String title;

    protected StoryLoader loader;

    protected Story story;

    private static final String SAVE_DATA_SECTION = "sectionId";

    private static final String SAVE_DATA_INVENTORY = "inventory";

    private static final String SAVE_DATA_PARAGRAPH_SWITCHES = "parSwitches";

    private static final String SAVE_DATA_LINK_SWITCHES = "linkSwitches";

    private static final String TEMP_SAVE_DATA_SECTION = "temp_sectionId";

    private static final String TEMP_SAVE_DATA_INVENTORY = "temp_inventory";

    private static final String TEMP_SAVE_DATA_PARAGRAPH_SWITCHES = "temp_parSwitches";

    private static final String TEMP_SAVE_DATA_LINK_SWITCHES = "temp_linkSwitches";

    private static final String TEMP_DATA_SAVED = "tempDataSaved";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loader = StoryLoader.getInstance(this);

        boolean stateWasSaved = false;
        if (savedInstanceState != null)
            stateWasSaved = savedInstanceState.getBoolean(TEMP_DATA_SAVED, false);

        if (stateWasSaved) {
            loadGame(true);
        } else {
            loader.init();
        }

        story = loader.getStory();
    }

    @Override
    public void onBackPressed() {
        this.story.proceedToQuit();
        displayText(story.getCurrentText());
    }

    protected abstract void displayText(List<String> text);

    protected abstract void processInput();

    @Override
    public void onClick(View v) {

        if (story.getPhase().equals(StoryPhase.QUIT)) {
            finish();
        } else if (story.getPhase().equals(StoryPhase.SAVING)) {
            saveGame(false);
            story.setPhase(StoryPhase.STARTED);
            displayText(story.getCurrentText());
            return;
        } else if (story.getPhase().equals(StoryPhase.LOADING)) {
            loadGame(false);
            story.setPhase(StoryPhase.STARTED);
            displayText(story.getCurrentText());
            return;
        }

        // se la storia è appena terminata, si passa alla sezione "La partita è giunta al termine..."
        // in modo che il giocatore possa premere lo schermo e scegliere se reiniziare, caricare, ecc.
        if(story.hasJustEnded()) {
            story.proceed();
            displayText(story.getCurrentText());
            return;
        }

        final boolean doesntNeedToRepeatText = story.doesntNeedToRepeatText();
        // se c'è un unico collegamento possibile, oppure se si tratta di una sezione temporanea, che
        // deve per forza tornare alla sezione chiamante, si procede direttamente alla prossima
        // sezione
        if (story.hasDirectOutcome() || doesntNeedToRepeatText) {
            story.proceed();
            // nel caso di una sezione temporanea (es: esamino, istruzioni...) non vogliamo
            // ripetere il testo della sezione chiamante, per cui passiamo direttamente alla verifica
            // degli input
            // se la storia è finita, bisogna mostrare il testo finale
            if (doesntNeedToRepeatText)
                processInput();
                // altrimenti vuol dire che siamo giunti in una nuova sezione, per cui ne mostriamo
                // il testo
            else
                displayText(story.getCurrentText());
            // altrimenti, si verificano tutti i possibili input per poter stabilire quale sia la
            // prossima sezione
        } else {
            processInput();
        }
    }

    /**
     * Salva il gioco.
     *
     * @param temporary True quando si tratta di un salvataggio temporaneo, dovuto all'interruzione
     *                  dell'app
     * @return True se il salvataggio è andato a buon fine, altrimenti false
     */
    private boolean saveGame(final boolean temporary) {
        final String sectionId = story.getSavingSectionId(temporary);
        if (sectionId != null) {
            final String inventoryItemIds = loader.stringifyInventory();
            final String paragraphSwitches = loader.stringifyParagraphSwitchesSoFar();
            final String linkSwitches = loader.stringifyLinkSwitchesSoFar();
            final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
            final SharedPreferences.Editor editor = preferences.edit();
            if (temporary) {
                editor.putString(TEMP_SAVE_DATA_SECTION, sectionId);
                editor.putString(TEMP_SAVE_DATA_INVENTORY, inventoryItemIds);
                editor.putString(TEMP_SAVE_DATA_PARAGRAPH_SWITCHES, paragraphSwitches);
                editor.putString(TEMP_SAVE_DATA_LINK_SWITCHES, linkSwitches);
            } else {
                editor.putString(SAVE_DATA_SECTION, sectionId);
                editor.putString(SAVE_DATA_INVENTORY, inventoryItemIds);
                editor.putString(SAVE_DATA_PARAGRAPH_SWITCHES, paragraphSwitches);
                editor.putString(SAVE_DATA_LINK_SWITCHES, linkSwitches);
            }
            editor.commit();
            return true;
        }
        return false;
    }

    /**
     * Carica il gioco.
     *
     * @param temporary True quando si tratta di un caricamento di dati te, dovuto all'interruzione
     *                  dell'app
     */
    private void loadGame(final boolean temporary) {
        final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        final String defaultValue = Section.HOME_SECTION;
        final String emptyDefaultValue = "";
        String sectionId;
        String inventoryItemIds;
        String paragraphSwitches;
        String linkSwitches;
        if (temporary) {
            sectionId = preferences.getString(TEMP_SAVE_DATA_SECTION, defaultValue);
            inventoryItemIds = preferences.getString(TEMP_SAVE_DATA_INVENTORY, emptyDefaultValue);
            paragraphSwitches = preferences.getString(TEMP_SAVE_DATA_PARAGRAPH_SWITCHES, emptyDefaultValue);
            linkSwitches = preferences.getString(TEMP_SAVE_DATA_LINK_SWITCHES, emptyDefaultValue);
        } else {
            sectionId = preferences.getString(SAVE_DATA_SECTION, defaultValue);
            inventoryItemIds = preferences.getString(SAVE_DATA_INVENTORY, emptyDefaultValue);
            paragraphSwitches = preferences.getString(SAVE_DATA_PARAGRAPH_SWITCHES, emptyDefaultValue);
            linkSwitches = preferences.getString(SAVE_DATA_LINK_SWITCHES, emptyDefaultValue);
        }
        loader.load(sectionId, inventoryItemIds, paragraphSwitches, linkSwitches);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        final boolean saveResult = saveGame(true);
        outState.putBoolean(TEMP_DATA_SAVED, saveResult);
        super.onSaveInstanceState(outState);
    }

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