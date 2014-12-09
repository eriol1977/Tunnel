package com.example.francesco.tunnel.story;

import java.util.List;

/**
 * Created by Francesco on 06/12/2014.
 */
public class Join {

    private final String id;

    private final String words;

    private final List<Item> items;

    private final String targetSectionId;

    Join(final String id, final String words, final List<Item> items, final String targetSectionId) {
        this.id = id;
        this.words = words;
        this.items = items;
        this.targetSectionId = targetSectionId;
    }

    /**
     * Verifica la compatibilità dei termini informati con la struttura interna del Join.
     * La verifica ha successo se:
     * - l'input contiene tutte le parole associate al Join
     * oppure
     * - l'input contiene i nomi di tutti gli oggetti associati al Join
     * e inoltre
     * - il personaggio possiede tutti gli oggetti associati al Join
     *
     * @param input
     * @return risultato del controllo
     */
    boolean check(final String input, final Inventory inventory) {
        boolean result = false;
        if (inventory.checkItems(this.items)) {
            result = Checker.checkWords(input.split("\\s+"), this.words.split("\\s+"));
            if (!result) {
                final int itemCount = this.items.size();
                int checked = 0;
                for (final Item item : this.items) {
                    if (item.check(input))
                        checked++;
                }
                result = (checked == itemCount);
            }
        }
        return result;
    }

    /**
     * @param inventory
     * @return True se nell'inventario ci sono tutti gli oggetti necessari all'unione
     */
    boolean check(final Inventory inventory) {
        return inventory.checkItems(this.items);
    }

    String getTargetSectionId() {
        return targetSectionId;
    }

    String getWords() {
        return words;
    }
}
