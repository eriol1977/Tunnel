package com.example.francesco.tunnel.activity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.francesco.tunnel.R;
import com.example.francesco.tunnel.story.Command;
import com.example.francesco.tunnel.story.Commands;
import com.example.francesco.tunnel.story.StoryPhase;
import com.example.francesco.tunnel.util.TTSBacked;
import com.example.francesco.tunnel.util.TTSUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 *
 */
public class HearStoryTellerActivity extends StoryTellerActivity implements TTSBacked {

    private TTSUtil ttsUtil;

    private ScrollView scrollView;

    private TextView textView;

    private TextView commandsView;

    private static final String STOP_EARCON = "[stop]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hear_story_teller);

        scrollView = (ScrollView) findViewById(R.id.content_scroll);

        textView = (TextView) findViewById(R.id.textView);
        textView.setTextSize(24);
        textView.setOnClickListener(this);

        commandsView = (TextView) findViewById(R.id.commandsView);
        commandsView.setTextSize(24);
        commandsView.setOnTouchListener(new MyOnTouchListener());

        ttsUtil = new TTSUtil(this, this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ttsUtil.onRestart();
    }

    /**
     * "Pulisce" il testo in italiano dagli accenti collocati per aiutare il
     * TextToSpeech con la pronuncia.
     *
     * @param text
     */
    private String cleanItalianText(final String text) {
        StringBuilder cleanText = new StringBuilder();
        final String[] words = text.split("\\s+");
        String cleanWord;
        for (final String word : words) {
            if (word.contains("è") && !word.endsWith("è"))
                cleanWord = word.replaceAll("è", "e");
            else if (word.contains("ò") && !word.endsWith("ò"))
                cleanWord = word.replaceAll("ò", "o");
            else if (word.contains("ì") && !word.endsWith("ì"))
                cleanWord = word.replaceAll("ì", "i");
            else if (word.contains("ù") && !word.endsWith("ù"))
                cleanWord = word.replaceAll("ù", "u");
            else if (word.contains("à") && !word.endsWith("à"))
                cleanWord = word.replaceAll("à", "a");
            else
                cleanWord = word;
            cleanText.append(cleanWord).append(" ");
        }
        return cleanText.toString();
    }

    @Override
    protected void displayText(final List<String> text) {
        commandsView.setVisibility(View.INVISIBLE);
        textView.setText("");
        textView.setVisibility(View.VISIBLE);
        scrollView.setScrollY(0);
        for (final String paragraph : text) {
            if (paragraph != null) { // non dovrebbe mai succedere, ma...
                textView.append(cleanItalianText(paragraph));
                //textView.append(Locale.getDefault().equals(Locale.ITALY) ? cleanItalianText(paragraph) : paragraph); // FIXME altre lingue
                textView.append("\n\n");
            }
        }

        ttsUtil.playSilence(10, TextToSpeech.QUEUE_FLUSH, null);
        for (String paragraph : text) {
            if (paragraph != null) { // non dovrebbe mai succedere, ma...
                ttsUtil.speak(paragraph);
                ttsUtil.playSilence(750, TextToSpeech.QUEUE_ADD, null);
            }
        }

    }

    private void playStopSound() {
        ttsUtil.playEarcon(STOP_EARCON, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onClick(View v) {
        ttsUtil.stop(); // interrompe la voce
        super.onClick(v);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ttsUtil.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        ttsUtil.onStop();
        super.onStop();
    }

    private List<String> commandInputs;

    private List<String> defaultCommandInputs;

    private int commandIndex = -1;

    private int defaultCommandIndex = -1;

    private String selectedCommandInput;

    private String previousCommandInput;

    private String previousSectionId;

    @Override
    protected void processInput() {
        if (!story.getPhase().equals(StoryPhase.QUIT)) { // per evitare che pronunci l'ultimo OK...
            textView.setVisibility(View.INVISIBLE);
            commandsView.setVisibility(View.VISIBLE);
            loadCommands();
            selectStartingCommand();
            displayCommand();
        }
    }

    private void loadCommands() {
        this.commandInputs = story.getCommandInputs();

        this.defaultCommandInputs = loader.getDefaultCommands();
        if (!thereAreSavedGames())
            this.defaultCommandInputs.remove(loader.command(Commands.LOAD_GAME).getFullName());
    }

    /**
     * Seleziona il comando iniziale quando il giocatore entra nella sezione.
     * <p/>
     * Se esistono comandi propri della sezione, il comando iniziale viene scelto tra di essi.
     * Se in precedenza non esisteva alcun comando di sezione, o se l'indice di tale comando non
     * è compatibile con la lista di comandi della nuova sezione, viene scelto il primo comando
     * della lista.
     * Se il giocatore è tornato alla stessa sezione di prima, riseleziona l'ultimo comando usato, se
     * ancora disponibile.
     * <p/>
     * Se non esistono comandi propri della sezione, il comando scelto è il primo di quelli standard.
     */
    private void selectStartingCommand() {
        if (this.commandInputs != null && !this.commandInputs.isEmpty()) {
            if (this.commandIndex == -1 || this.commandIndex > this.commandInputs.size() - 1)
                this.commandIndex = 0;
            else if (previousSectionId != null && previousSectionId.equals(story.getCurrent().getId())
                    && previousCommandInput != null && this.commandInputs.contains(previousCommandInput))
                this.commandIndex = this.commandInputs.indexOf(previousCommandInput);
            else
                this.commandIndex = 0;
            this.selectedCommandInput = this.commandInputs.get(this.commandIndex);
            return;
        } else {
            this.commandIndex = -1;
        }

        if (this.defaultCommandInputs != null && !this.defaultCommandInputs.isEmpty()) {
            this.defaultCommandIndex = 0;
            this.selectedCommandInput = this.defaultCommandInputs.get(this.defaultCommandIndex);
            return;
        } else {
            this.defaultCommandIndex = -1;
        }

        this.selectedCommandInput = null;
    }

    void displayCommand() {
        if (selectedCommandInput != null) {
            commandsView.setText(selectedCommandInput);
            ttsUtil.playSilence(10, TextToSpeech.QUEUE_FLUSH, null);
            ttsUtil.speak(selectedCommandInput);
        }
    }

    @Override
    public void afterTTSInit() {
        ttsUtil.addEarcon(STOP_EARCON, "com.example.francesco.tunnel", R.raw.beep);
        displayText(story.getCurrentText()); // testo home
    }

    @Override
    public void afterUtteranceCompleted(String utteranceId) {
        // do nothing
    }

    class MyOnTouchListener implements View.OnTouchListener {

        private final GestureDetectorCompat mDetector;

        MyOnTouchListener() {
            mDetector = new GestureDetectorCompat(HearStoryTellerActivity.this, new MyGestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return this.mDetector.onTouchEvent(event);
        }
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (selectedCommandInput != null) {
                previousCommandInput = selectedCommandInput;
                if (story.getCurrent() != null)
                    previousSectionId = story.getCurrent().getId();
                story.proceed(selectedCommandInput);
                displayText(story.getCurrentText());
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            // fling orizzontale
            if (Math.abs(velocityX) > Math.abs(velocityY)) {
                if (!commandInputs.isEmpty()) {
                    // fling verso destra
                    if (velocityX > 0) {
                        commandIndex--;
                        if (commandIndex < 0)
                            commandIndex = commandInputs.size() - 1;
                        // fling verso sinistra
                    } else {
                        commandIndex++;
                        if (commandIndex >= commandInputs.size())
                            commandIndex = 0;
                    }
                    selectedCommandInput = commandInputs.get(commandIndex);
                    displayCommand();
                } else {
                    playStopSound();
                }
                // fling verticale
            } else {
                if (!defaultCommandInputs.isEmpty()) {
                    // fling verso il basso
                    if (velocityY > 0) {
                        defaultCommandIndex++;
                        if (defaultCommandIndex >= defaultCommandInputs.size())
                            defaultCommandIndex = 0;
                        // fling verso l'alto
                    } else {
                        defaultCommandIndex--;
                        if (defaultCommandIndex < 0)
                            defaultCommandIndex = defaultCommandInputs.size() - 1;
                    }
                    selectedCommandInput = defaultCommandInputs.get(defaultCommandIndex);
                    displayCommand();
                } else {
                    playStopSound();
                }
            }
            return true;
        }
    }
}
