package io.fabianterhorst.floppy;

/**
 * Created by fabianterhorst on 22.09.16.
 */

public abstract class OnFindListener<T> {
    public abstract boolean isObject(T object);
}
