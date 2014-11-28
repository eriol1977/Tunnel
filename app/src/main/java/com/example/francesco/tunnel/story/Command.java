package com.example.francesco.tunnel.story;

/**
 * Created by Francesco on 21/11/2014.
 */
public class Command {

    private final String id;

    private final String[] commandWords;

    Command(final String id, final String[] commandWords) {
        this.id = id;
        this.commandWords = commandWords;
    }

    String getId() {
        return id;
    }

    /**
     * @param words
     * @return True se ognuno dei termini registrati nel comando (ex: "torna gioco") compare
     * nella String di parole informata come parametro (ex: "torna al gioco")
     */
    boolean check(final String words) {
        int found = 0;
        final String[] everyWord = words.split("\\s+");
        for (String word : everyWord) {
            for (String commandWord : this.commandWords) {
                if(word.equalsIgnoreCase(commandWord)) {
                    found++;
                    break;
                }
            }
        }
        return found == this.commandWords.length;
    }

    public String getCommandWords() {
        final StringBuilder sb = new StringBuilder();
        for(final String commandWord: this.commandWords) {
            sb.append(commandWord).append(" ");
        }
        return sb.toString();
    }
}
