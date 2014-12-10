package com.example.francesco.tunnel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.francesco.tunnel.R;
import com.example.francesco.tunnel.story.StoryPhase;

import java.util.List;
import java.util.Locale;

/**
 * TODOs
 * - implementare notes_get, in modo che una nota marcata come "rimossa" dal notes_drop non
 * venga mai più aggiunta al rientrare nella sezione che descrive un oggetto
 */
public class HearStoryTellerActivity extends StoryTellerActivity {

    private TextToSpeech tts;

    private TextView textView;

    private TextView commandsView;

    private static final int CHECK_TTS = 2222;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hear_story_teller);

        textView = (TextView) findViewById(R.id.textView);
        textView.setTextSize(24);
        textView.setVerticalScrollBarEnabled(true);
        textView.setOnClickListener(this);

        commandsView = (TextView) findViewById(R.id.commandsView);
        commandsView.setTextSize(24);
        commandsView.setOnTouchListener(new MyOnTouchListener());

        checkTts();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkTts();
    }

    private void checkTts() {
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, CHECK_TTS);
    }

    @Override
    protected void displayText(List<String> text) {
        commandsView.setVisibility(View.INVISIBLE);
        textView.setText("");
        textView.setVisibility(View.VISIBLE);
        for (final String s : text) {
            if (s != null) { // non dovrebbe mai succedere, ma...
                textView.append(s);
                textView.append("\n\n");
            }
        }

        tts.playSilence(10, TextToSpeech.QUEUE_FLUSH, null);
        for (String paragraph : text) {
            if (paragraph != null) { // non dovrebbe mai succedere, ma...
                speak(paragraph);
                tts.playSilence(750, TextToSpeech.QUEUE_ADD, null);
            }
        }
    }

    private void speak(final String text) {
        tts.speak(text, TextToSpeech.QUEUE_ADD, null);
    }

    @Override
    public void onClick(View v) {
        tts.stop(); // interrompe la voce
        super.onClick(v);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHECK_TTS) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                tts = new TextToSpeech(this.getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            if (tts.isLanguageAvailable(Locale.ITALIAN) == TextToSpeech.LANG_AVAILABLE) {
                                tts.setLanguage(Locale.ITALIAN);
                                displayText(story.getCurrentText()); // testo home
                            } else {
                                finish();
                            }
                        }
                    }
                });
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        tts.stop();
        tts.shutdown();
        super.onStop();
    }

    private List<String> commandInputs;

    private List<String> defaultCommandInputs;

    private int commandIndex;

    private int defaultCommandIndex;

    private String selectedCommandInput;

    @Override
    protected void processInput() {
        if (!story.getPhase().equals(StoryPhase.QUIT)) { // per evitare che pronunci l'ultimo OK...
            textView.setVisibility(View.INVISIBLE);
            commandsView.setVisibility(View.VISIBLE);

            this.commandInputs = story.getCommandInputs();
            this.defaultCommandInputs = loader.getDefaultCommands();
            this.commandIndex = 0;
            this.defaultCommandIndex = 0;

            if (!this.commandInputs.isEmpty()) {
                this.selectedCommandInput = this.commandInputs.get(0);
            } else if (!this.defaultCommandInputs.isEmpty()) {
                this.selectedCommandInput = this.defaultCommandInputs.get(0);
            }
            displayCommand();
        }
    }

    void displayCommand() {
        if (selectedCommandInput != null) {
            commandsView.setText(selectedCommandInput);
            tts.playSilence(10, TextToSpeech.QUEUE_FLUSH, null);
            speak(selectedCommandInput);
        }
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
        public boolean onDoubleTap(MotionEvent e) {
            story.proceed(selectedCommandInput);
            displayText(story.getCurrentText());
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
                }
            }
            displayCommand();
            return true;
        }
    }
}
