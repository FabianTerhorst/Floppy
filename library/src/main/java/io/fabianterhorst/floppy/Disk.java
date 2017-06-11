package io.fabianterhorst.floppy;

import org.nustaq.serialization.FSTConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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

    private final Map<String, OnWriteListener> mCallbacks = new HashMap<>();

    private final Map<String, File> mFiles = new HashMap<>();

    private final String mName;

    private final String mPath;

    private final String mFilesDir;

    Disk(String name, String path, FSTConfiguration config) {
        this.mName = name;
        this.mPath = path;
        this.mConfig = config;
        this.mFilesDir = createDiskDir();
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
            if (bytes.length == 0) {
                return defaultObject;
            }
            T object = (T) mConfig.asObject(bytes);
            bufferedSource.close();
            return object;
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
        if (!deleted) {
            throw new RuntimeException("Couldn't delete file " + originalFile
                    + " for table " + key);
        }
    }

    public void deleteAll() {
        final String dbPath = getDbPath();
        if (!deleteDirectory(dbPath)) {
            System.out.print("Couldn't delete Floppy dir " + dbPath);
        }
    }

    private File getOriginalFile(String key) {
        File file = mFiles.get(key);
        if (file == null) {
            file = new File(mFilesDir + File.separator + key + ".pt");
            mFiles.put(key, file);
        }
        return file;
    }

    private String getDbPath() {
        return mPath + File.separator + mName;
    }

    private String createDiskDir() {
        String filesDir = getDbPath();
        if (!new File(filesDir).exists()) {
            boolean isReady = new File(filesDir).mkdirs();
            if (!isReady) {
                throw new RuntimeException("Couldn't create Floppy dir: " + filesDir);
            }
        }
        return filesDir;
    }

    private static boolean deleteDirectory(String dirPath) {
        File directory = new File(dirPath);
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (null != files) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file.toString());
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
