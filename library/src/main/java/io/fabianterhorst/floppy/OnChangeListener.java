package io.fabianterhorst.floppy;

/**
 * Created by fabianterhorst on 22.09.16.
 */

public abstract class OnChangeListener<T> {
    public abstract void onChange(T object);
}
