package io.fabianterhorst.floppy;

/**
 * Created by fabianterhorst on 19.09.16.
 */

public interface OnWriteListener<T> {
    void onWrite(T object);
}
