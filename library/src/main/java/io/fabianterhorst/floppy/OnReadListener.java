package io.fabianterhorst.floppy;

/**
 * Created by fabianterhorst on 22.09.16.
 */

public interface OnReadListener<T> {
    T onRead(T object);
}
