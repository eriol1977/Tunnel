package com.example.francesco.tunnel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;

import com.example.francesco.tunnel.R;

import java.util.List;
import java.util.Locale;

public abstract class VoiceStoryTellerActivity extends StoryTellerActivity {

    private TextToSpeech tts;

    private static final int GET_SPEECH = 1111;

    private static final int CHECK_TTS = 2222;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkTts();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkTts();
    }

    private void checkTts()
    {
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, CHECK_TTS);
    }

    protected void displayText(List<String> text) {
        for (String paragraph : text) {
            speak(paragraph);
            tts.playSilence(750, TextToSpeech.QUEUE_ADD, null);
        }
    }

    private void speak(final String text) {
        tts.speak(text, TextToSpeech.QUEUE_ADD, null);
    }

    protected void processInput() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, R.string.l_prompt);
        startActivityForResult(i, GET_SPEECH);
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
        } else if (requestCode == GET_SPEECH && resultCode == RESULT_OK) {
            String speech = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
            story.proceed(speech);
            displayText(story.getCurrentText());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        tts.stop();
        tts.shutdown();
        super.onStop();
    }
}