package com.example.francesco.tunnel.story;

/**
 * Created by Francesco on 06/12/2014.
 */
public class Checker {

    /**
     * @param inputWords
     * @param wordsToFind
     * @return True se l'array inputWords contiene tutte le parole dell'array wordsToFind
     */
    static boolean checkWords(final String[] inputWords, final String[] wordsToFind) {
        final int allWordsToFind = wordsToFind.length;
        int foundWords = 0;
        for (String inputWord : inputWords) {
            for (String wordToFind : wordsToFind) {
                if (inputWord.equalsIgnoreCase(wordToFind)) {
                    foundWords++;
                    break;
                }
            }
        }
        return foundWords == allWordsToFind;
    }
}
