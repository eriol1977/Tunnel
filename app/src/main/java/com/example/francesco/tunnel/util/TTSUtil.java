package com.example.francesco.tunnel.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.speech.tts.TextToSpeech;

import com.example.francesco.tunnel.R;

import java.util.HashMap;
import java.util.Locale;

/**
 * Utilitario per tutte le Activity che facciano uso di TextToSpeech.
 * Fornisce i metodi speak(), playSilence(), playEarcon() e stop() per parlare e interrompere, oltre a occuparsi di tutta la
 * parte di inizializzazione e gestione del servizio.
 *
 * Nell'Activity, ricordarsi di:
 * - implementare {@link com.example.francesco.tunnel.util.TTSBacked}
 * - invocare ttsUtil.onActivityResult(requestCode, resultCode, data) all'interno del metodo
 *   onActivityResult (creare il metodo se non esiste)
 * - invocare ttsUtil.onRestart() all'interno del metodo onRestart
 * - invocare ttsUtil.onStop() all'interno del metodo onStop
 *
 * Created by Francesco on 17/12/2014.
 */
public class TTSUtil implements TextToSpeech.OnInitListener {

    private final Activity activity;

    private final TTSBacked ttsBacked;

    private TextToSpeech tts;

    private HashMap<String, String> webConnectedVoice;

    private static final int CHECK_TTS = 2222;

    public TTSUtil(final Activity activity, final TTSBacked ttsBacked) {
        this.activity = activity;
        this.ttsBacked = ttsBacked;
        init();
    }

    private void init() {
        webConnectedVoice = new HashMap<String, String>();
        webConnectedVoice.put(TextToSpeech.Engine.KEY_FEATURE_NETWORK_SYNTHESIS, "true");
        checkTts();
    }

    private void checkTts() {
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        activity.startActivityForResult(checkIntent, CHECK_TTS);
    }

    public void speak(final String text) {
        if (webConnectionActive())
            tts.speak(text, TextToSpeech.QUEUE_ADD, webConnectedVoice);
        else
            tts.speak(text, TextToSpeech.QUEUE_ADD, null);
    }

    public boolean isSpeaking() {
        return tts.isSpeaking();
    }

    public void playSilence(final long durationInMs, final int queueMode, final HashMap<String, String> params) {
        tts.playSilence(durationInMs, queueMode, params);
    }

    public void addEarcon(final String earcon, final String packagename, final int resourceId) {
        tts.addEarcon(earcon, packagename, resourceId);
    }

    public void playEarcon(final String earcon, final int queueMode, final HashMap<String, String> params) {
        tts.playEarcon(earcon, queueMode, params);
    }

    public void stop() {
        tts.stop();
    }

    private boolean webConnectionActive() {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHECK_TTS) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                tts = new TextToSpeech(activity.getApplicationContext(), this);
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                activity.startActivity(installIntent);
            }
        }
    }

    public void onRestart() {
        checkTts();
    }

    public void onStop() {
        tts.stop();
        tts.shutdown();
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
            ttsBacked.afterTTSInit();
//                                } else {
//                                    finish(); // FIXME messaggio errore scritto?
//                                }
//                            } else {
//                                finish(); // FIXME messaggio errore scritto?
//                            }
        } else {
            activity.finish(); // FIXME messaggio errore scritto?
        }
    }


}
