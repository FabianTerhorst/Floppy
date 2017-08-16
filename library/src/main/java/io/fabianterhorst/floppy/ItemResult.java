package io.fabianterhorst.floppy;

/**
 * Created by fabianterhorst on 22.09.16.
 */

class ItemResult<T> {
    T item;
    int index;

    ItemResult(T item, int index) {
        this.item = item;
        this.index = index;
    }
}
