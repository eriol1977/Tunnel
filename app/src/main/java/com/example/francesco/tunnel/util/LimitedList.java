package com.example.francesco.tunnel.util;

import java.util.ArrayList;

/**
 * ArrayList che accetta un numero definito di elementi, dopodiché non ne accetta altri.
 * Può essere bloccato e sbloccato per smettere del tutto di accettare elementi.
 *
 * <p/>
 * Created by Francesco on 24/12/2014.
 */
public class LimitedList<E> extends ArrayList<E> {

    private final int maxElements;

    private int lock;

    public LimitedList(final int maxElements) {
        this.maxElements = maxElements;
    }

    public void unlock() {
        clear();
        lock = maxElements;
    }

    public void lock() {
        lock = 0;
    }

    @Override
    public boolean add(final E object) {
        if (size() >= lock)
            return false;
        return super.add(object);
    }
}
