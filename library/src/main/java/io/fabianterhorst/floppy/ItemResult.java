package io.fabianterhorst.floppy;

/**
 * Created by fabianterhorst on 22.09.16.
 */

class ItemResult<T> {
    private T item;
    private int index;

    ItemResult(T item, int index) {
        this.item = item;
        this.index = index;
    }

    public T getItem() {
        return item;
    }

    public int getIndex() {
        return index;
    }
}
