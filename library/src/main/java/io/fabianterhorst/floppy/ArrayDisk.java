package io.fabianterhorst.floppy;

import org.nustaq.serialization.FSTConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fabianterhorst on 22.09.16.
 */
//Todo : add chang listener for add and remove as well with add remove method
public class ArrayDisk extends MemoryDisk {

    private final Map<String, OnEqualListener> equalListeners = new HashMap<>();

    private final Map<String, OnChangeListener> changeListeners = new HashMap<>();

    ArrayDisk(String name, String path, FSTConfiguration config) {
        super(name, path, config);
    }

    public void addOnEqualListener(String key, OnEqualListener listener) {
        equalListeners.put(key, listener);
    }

    public void addOnChangeListener(String key, OnChangeListener listener) {
        changeListeners.put(key, listener);
    }

    public <T> void changeItem(String key, T object) {
        ArrayList<T> items = read(key);
        items.set(getItemIndex(key, object), object);
        write(key, items);
        callCallbacks(key, object);
    }

    public <T> void addItem(String key, T object) {
        ArrayList<T> items = read(key);
        items.add(object);
        write(key, items);
    }

    public <T> void removeItem(String key, T object) {
       removeItem(key, getItemIndex(key, object));
    }

    public <T> void removeItem(String key, int index) {
        ArrayList<T> items = read(key);
        items.remove(index);
        write(key, items);
    }

    @SuppressWarnings("unchecked")
    private <T> int getItemIndex(String key, T object) {
        ArrayList<T> items = read(key);
        OnEqualListener<T> listener = (OnEqualListener<T>) equalListeners.get(key);
        T item;
        for (int i = 0, size = items.size(); i < size; i++) {
            item = items.get(i);
            if (listener.isEqual(item, object)) {
                return i;
            }
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    private synchronized <T> void callCallbacks(String key, T object) {
        if (changeListeners.size() > 0) {
            OnChangeListener<T> listener = (OnChangeListener<T>) changeListeners.get(key);
            if (listener != null) {
                listener.onChange(object);
            }
        }
    }
}
