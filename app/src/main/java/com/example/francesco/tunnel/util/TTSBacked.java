package com.example.francesco.tunnel.util;

/**
 * La classe {{@link com.example.francesco.tunnel.util.TTSUtil} ha bisogno di una classe che implementi
 * questa interfaccia.
 *
 * Created by Francesco on 17/12/2014.
 */
public interface TTSBacked {

    /**
     * Indica cosa deve fare TTSUtil immediatamente dopo l'inizializzazione del TextToSpeech
     */
    void afterTTSInit();
}
