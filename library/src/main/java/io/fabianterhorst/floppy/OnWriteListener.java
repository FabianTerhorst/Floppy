package io.fabianterhorst.floppy;

/**
 * Created by fabianterhorst on 19.09.16.
 */

public abstract class OnWriteListener<Object> {
    public abstract void onWrite(Object object);
}
