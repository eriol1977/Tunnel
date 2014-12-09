package com.example.francesco.tunnel.story;

import java.util.List;

/**
 * Created by Francesco on 18/11/2014.
 */
public class Link {

    /**
     * id all'interno della sezione (Link di sezioni differenti possono anche avere lo stesso id)
     */
    private final String id;

    /**
     * Sezione narrativa alla quale il link appartiene
     */
    private final Section section;

    /**
     * Id della sezione linkata
     */
    private String nextSection;

    /**
     * Ids di uno o più comandi necessari per percorrere il link (opzionale)
     */
    private String[] commandIds;

    /**
     * Ids di uno o più oggetti necessari per percorrere il link (opzionale)
     */
    private String[] itemIds;

    /**
     * Ids di uno o più oggetti la cui assenza dall'inventario è una condizione per percorrere il link (opzionale)
     */
    private String[] noItemIds;

    Link(final String id, final Section section, final String nextSection) {
        this.id = id;
        this.section = section;
        this.nextSection = nextSection;
    }

    /**
     * Confronta il contenuto del comando con le proprie restrizioni di comando/oggetto,
     * verificando se il link può essere percorso o meno.
     *
     * @param words
     * @return True False
     */
    boolean check(final String words) {
        StoryLoader sl = StoryLoader.getInstance();

        /**
         * se il link dipende da uno o più possibili comandi, almeno uno di essi deve comparire
         * nella String informata
         */
        boolean commandFound = false;
        if (this.commandIds == null || this.commandIds.length == 0) {
            commandFound = true;
        } else {
            for (final String commandId : this.commandIds) {
                commandFound = sl.command(commandId).check(words);
                if (commandFound)
                    break;
            }
        }

        boolean itemFound = false;
        if ((this.itemIds == null || this.itemIds.length == 0) && (this.noItemIds == null || this.noItemIds.length == 0)) {
            itemFound = true;
        }
        /**
         * se il comando è "prendo" o "uso", il nome dell'oggetto deve comparire nella String informata
         * (si suppone che l'oggetto da prendere sia uno solo, nella struttura del link)
         */
        else if (this.commandIds.length == 1 && (this.commandIds[0].equals(Commands.GET) || this.commandIds[0].equals(Commands.USE) || this.commandIds[0].equals(Commands.OBSERVE))) {
            Item item = sl.item(this.itemIds[0]);
            itemFound = item.check(words);
        }
//        /**
//         * se il comando è "uso", l'oggetto deve far parte dell'inventario del giocatore, o essere
//         * presente nella sezione narrativa attuale, e il suo nome deve comparire nella String informata
//         * (si suppone che l'oggetto da usare sia uno solo, nella struttura del link)
//         */
//        else if (this.commandIds.length == 1 && this.commandIds[0].equals(Commands.USE)) {
//            final Inventory inventory = sl.getCharacter().getInventory();
//            Item item = sl.item(this.itemIds[0]);
//            itemFound = (inventory.checkItem(item) || section.checkUsableItem(item)) && item.check(words);
//        }
        /**
         * se il comando è un altro, si suppone che il giocatore debba possedere gli oggetti
         * elencati nel proprio inventario, o al contrario, che non li debba possedere.
         */
        else {
            final Inventory inventory = sl.getCharacter().getInventory();
            Item item;
            // verifica che l'inventario contenga tutti gli oggetti da possedere
            boolean toHave = true;
            for (final String itemId : this.itemIds) {
                item = sl.item(itemId);
                toHave = inventory.checkItem(item);
                if (!toHave)
                    break;
            }
            // verifica che l'inventario non contenga nessuno degli oggetti da non possedere
            boolean notToHave = true;
            for (final String itemId : this.noItemIds) {
                item = sl.item(itemId);
                notToHave = !inventory.checkItem(item);
                if (!notToHave)
                    break;
            }
            itemFound = toHave && notToHave;
        }

        return commandFound && itemFound;
    }

    String getNextSection() {
        return nextSection;
    }

    void setNextSection(String nextSection) {
        this.nextSection = nextSection;
    }

    void setCommandIds(String... commandIds) {
        this.commandIds = commandIds;
    }

    String[] getCommandIds() {
        // per evitare ripetizioni di comandi, siccome c'è un link "gemello"
        if(this.noItemIds != null && this.noItemIds.length > 0)
            return new String[]{};
        return commandIds;
    }

    String[] getItemIds() {
        return itemIds;
    }

    void setItemIds(List<String> itemIds) {
        this.itemIds = new String[itemIds.size()];
        for(int i = 0; i < itemIds.size(); i++) {
            this.itemIds[i] = itemIds.get(i);
        }
    }

    void setItemIds(String... itemIds) {
        this.itemIds = itemIds;
    }

    void setNoItemIds(List<String> noItemIds) {
        this.noItemIds = new String[noItemIds.size()];
        for(int i = 0; i < noItemIds.size(); i++) {
            this.noItemIds[i] = noItemIds.get(i);
        }
    }

    String getId() {
        return id;
    }
}
