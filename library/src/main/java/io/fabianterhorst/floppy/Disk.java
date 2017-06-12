package io.fabianterhorst.floppy;

import org.nustaq.serialization.FSTConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by fabianterhorst on 18.09.16.
 */

//Todo: transform exceptions to own exceptions and forward to app
public class Disk {

    private final FSTConfiguration mConfig;

    private final Map<String, OnWriteListener> mCallbacks = new LinkedHashMap<>();

    private final Map<String, File> mFiles = new LinkedHashMap<>();

    private final File mFile;

    Disk(String name, String path, FSTConfiguration config) {
        this.mConfig = config;
        this.mFile = new File(path, name);
        if (!mFile.exists()) {
            if (!mFile.mkdirs()) {
                System.out.print("Couldn't create Floppy dir: " + mFile);
            }
        }
    }

    public void write(String key, Object object) {
        final File originalFile = getOriginalFile(key);
        try {
            BufferedSink bufferedSink = Okio.buffer(Okio.sink(originalFile));
            bufferedSink.write(mConfig.asByteArray(object));
            bufferedSink.flush();
            bufferedSink.close(); //also close file stream ?sync maybe?
            callCallbacks(key, object);
        } catch (IOException io) {
            io.printStackTrace();//Todo: give back
        }
    }

    public <T> T read(String key) {
        return read(key, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T read(String key, T defaultObject) {
        final File originalFile = getOriginalFile(key);
        if (!originalFile.exists()) {
            return defaultObject;
        }
        try {
            BufferedSource bufferedSource = Okio.buffer(Okio.source(originalFile));
            byte[] bytes = bufferedSource.readByteArray();
            bufferedSource.close();
            if (bytes.length == 0) {
                return defaultObject;
            }
            return (T) mConfig.asObject(bytes);
        } catch (IOException io) {
            io.printStackTrace();
            return defaultObject;
        }
    }

    public synchronized void addOnWriteListener(String key, OnWriteListener onWriteListener) {
        mCallbacks.put(key, onWriteListener);
    }

    public synchronized void removeListener(String key) {
        mCallbacks.remove(key);
    }

    @SuppressWarnings("unchecked")
    private synchronized <T> void callCallbacks(String key, T object) {
        if (mCallbacks.size() > 0) {
            OnWriteListener<T> listener = (OnWriteListener<T>) mCallbacks.get(key);
            if (listener != null) {
                listener.onWrite(object);
            }
        }
    }

    public void delete(String key) {
        final File originalFile = getOriginalFile(key);
        if (!originalFile.exists()) {
            return;
        }

        boolean deleted = originalFile.delete();
        mFiles.remove(key);
        if (!deleted) {
            System.out.print("Couldn't delete file " + originalFile
                    + " for table " + key);
        }
    }

    public void deleteAll() {
        if (!deleteDirectory(mFile)) {
            System.out.print("Couldn't delete Floppy dir " + mFile.toString());
        }
    }

    private File getOriginalFile(String key) {
        File file = mFiles.get(key);
        if (file == null) {
            file = new File(mFile, key + ".pt");
            mFiles.put(key, file);
        }
        return file;
    }

    private static boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (null != files) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        //noinspection ResultOfMethodCallIgnored
                        file.delete();
                    }
                }
            }
        }
        return directory.delete();
    }
}
