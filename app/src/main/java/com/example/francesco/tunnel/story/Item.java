package com.example.francesco.tunnel.story;

/**
 * Created by Francesco on 20/11/2014.
 */
public class Item {

    final private String id;

    final private String name;

    final private String description;

    Item(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    String getId() {
        return id;
    }

    String getName() {
        return name;
    }

    String getDescription() {
        return description;
    }

    /**
     * @param words
     * @return True se il nome dell'oggetto (ex: "chiave") compare nella String di parole informata
     * come parametro (ex:"chiave dorata")
     */
    boolean check(final String words) {
        boolean found = false;
        final String[] everyWord = words.split("\\s+");
        for (String word : everyWord) {
            if(word.equalsIgnoreCase(this.name)) {
                found = true;
                break;
            }
        }
        return found;
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
