package io.fabianterhorst.floppy;

/**
 * Created by fabianterhorst on 22.09.16.
 */

public abstract class OnReadListener<T> {
    public abstract T onRead(T object);
}
