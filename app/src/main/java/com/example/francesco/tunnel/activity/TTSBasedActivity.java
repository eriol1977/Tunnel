package com.example.francesco.tunnel.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

import com.example.francesco.tunnel.R;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Francesco on 16/12/2014.
 */
public class TTSBasedActivity extends Activity implements TextToSpeech.OnInitListener {

    protected TextToSpeech tts;

    private HashMap<String, String> webConnectedVoice;

    private static final int CHECK_TTS = 2222;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webConnectedVoice = new HashMap<String, String>();
        webConnectedVoice.put(TextToSpeech.Engine.KEY_FEATURE_NETWORK_SYNTHESIS, "true");

        checkTts();
    }

    protected void speak(final String text) {
        if (webConnectionActive())
            tts.speak(text, TextToSpeech.QUEUE_ADD, webConnectedVoice);
        else
            tts.speak(text, TextToSpeech.QUEUE_ADD, null);
    }

    private void checkTts() {
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, CHECK_TTS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHECK_TTS) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                tts = new TextToSpeech(this.getApplicationContext(), this);
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean webConnectionActive() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    @Override
    protected void onStop() {
        tts.stop();
        tts.shutdown();
        super.onStop();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
//                            final Locale defaultLocale = Locale.getDefault();
//                            if (defaultLocale.equals(Locale.ITALY) || defaultLocale.toString().equals("pt_BR")) {
//                                final int languageAvailable = tts.isLanguageAvailable(defaultLocale);
//                                if (languageAvailable == TextToSpeech.LANG_AVAILABLE
//                                        || languageAvailable == TextToSpeech.LANG_COUNTRY_AVAILABLE
//                                        || languageAvailable == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE) {
            tts.setLanguage(Locale.ITALIAN);
//                                } else {
//                                    finish(); // FIXME messaggio errore scritto?
//                                }
//                            } else {
//                                finish(); // FIXME messaggio errore scritto?
//                            }
        } else {
            finish(); // FIXME messaggio errore scritto?
        }
    }
}
