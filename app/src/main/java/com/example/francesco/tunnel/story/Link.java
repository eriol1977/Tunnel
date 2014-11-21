package com.example.francesco.tunnel.story;

import java.util.List;

/**
 * Created by Francesco on 18/11/2014.
 */
public class Link {

    /**
     * Identifica univocamente il link dentro la sezione
     */
    private final String id;

    /**
     * Id della sezione linkata
     */
    private String nextSection;

    /**
     * Ids di uno o più comandi necessari per percorrere il link (opzionale)
     */
    private String[] commandIds;

    /**
     * Ids di uno o più oggetti necessario per percorrere il link (opzionale)
     */
    private String[] itemIds;

    public Link(final String id, final String nextSection) {
        this.id = id;
        this.nextSection = nextSection;
    }

    /**
     * TODO
     *
     * Confronta il contenuto del comando con le proprie restrizioni di comando/oggetto,
     * verificando se il link può essere percorso o meno.
     *
     * @param words
     * @return Id della sezione linkata, oppure null
     */
    public String check(final String words)
    {
        // verificare nel parametro informato la presenza di almeno una parola tra i comandi e gli oggetti del link
        // foreach commandId: Commands.get(commandId).check(words)
        // foreach itemId: character.getInventory().get(itemId).check(words)

        // se non ci sono né comandi né oggetti, il link è diretto e questo metodo non viene neppure chiamato

        return null;
    }

    public String getId() {
        return id;
    }

    public String getNextSection() {
        return nextSection;
    }

    public void setNextSection(String nextSection) {
        this.nextSection = nextSection;
    }

    public void setCommandIds(String... commandIds) {
        this.commandIds = commandIds;
    }

    public void setItemIds(String... itemIds) {
        this.itemIds = itemIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Link link = (Link) o;

        if (!id.equals(link.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
