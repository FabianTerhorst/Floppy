package io.fabianterhorst.floppy;

/**
 * Created by fabianterhorst on 22.09.16.
 */

public abstract class OnEqualListener<T> {
    public abstract boolean isEqual(T oldObject, T newObject);
}
