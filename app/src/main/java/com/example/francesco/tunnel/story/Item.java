package com.example.francesco.tunnel.story;

/**
 * Created by Francesco on 20/11/2014.
 */
public class Item {

    final private String id;

    final private String name;

    final private String sectionId;

    Item(final String id, final String name, final String sectionId) {
        this.id = id;
        this.name = name;
        this.sectionId = sectionId;
    }

    String getId() {
        return id;
    }

    String getName() {
        return name;
    }

    String getSectionId() {
        return sectionId;
    }

    /**
     * @param words
     * @return True se il nome dell'oggetto (ex: "chiave") compare nella String di parole informata
     * come parametro (ex:"chiave dorata").
     * Si considera anche il caso di nome oggetto formato da più termini, come "medaglione sole":
     * in questo caso il comando "prendo medaglione" non deve essere accettato, invece "prendo
     * medaglione del sole" o "uso medaglione col sole" sì.
     */
    boolean check(final String words) {
        final String[] nameWords = this.name.split("\\s+");
        final String[] commandWords = words.split("\\s+");
        return Checker.checkWords(commandWords, nameWords);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (!id.equals(item.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
