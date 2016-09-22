package io.fabianterhorst.floppy;

/**
 * Created by fabianterhorst on 22.09.16.
 */

public interface OnFindListener<T> {
    boolean isObject(T object);
}
