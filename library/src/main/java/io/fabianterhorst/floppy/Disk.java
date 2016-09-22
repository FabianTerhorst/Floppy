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
        write(key, object, true);
    }

    public void write(String key, Object object, boolean fast) {
        final File originalFile = getOriginalFile(key);
        File backupFile = null;
        if (!fast) {
            backupFile = makeBackupFile(originalFile);
            // Rename the current file so it may be used as a backup during the next read
            if (originalFile.exists()) {
                //Rename original to backup
                if (!backupFile.exists()) {
                    if (!originalFile.renameTo(backupFile)) {
                        throw new RuntimeException("Couldn't rename file " + originalFile
                                + " to backup file " + backupFile);
                    }
                } else {
                    //Backup exist -> original file is broken and must be deleted
                    //noinspection ResultOfMethodCallIgnored
                    originalFile.delete();
                }
            }
        }
        try {
            /*FileOutputStream stream = new FileOutputStream(originalFile);
            FSTObjectOutput output = mConfig.getObjectOutput(stream);
            output.writeObject(object);
            output.flush();
            stream.close();*/
            BufferedSink bufferedSink = Okio.buffer(Okio.sink(originalFile));
            bufferedSink.write(mConfig.asByteArray(object));
            bufferedSink.flush();
            bufferedSink.close(); //also close file stream
            // Writing was successful, delete the backup file if there is one.
            if (backupFile != null) {
                //noinspection ResultOfMethodCallIgnored
                backupFile.delete();
            }
            callCallbacks(key, object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T read(String key) {
        return read(key, true);
    }

    @SuppressWarnings("unchecked")
    public <T> T read(String key, boolean fast) {
        final File originalFile = getOriginalFile(key);
        if (!fast) {
            final File backupFile = makeBackupFile(originalFile);
            if (backupFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                originalFile.delete();
                //noinspection ResultOfMethodCallIgnored
                backupFile.renameTo(originalFile);
            }
        }

        if (!originalFile.exists()) {
            return null;
        }

        try {
            /*FileInputStream stream = new FileInputStream(originalFile);
            FSTObjectInput input = mConfig.getObjectInput(stream);
            stream.close();
            return (T) input.readObject();*/
            BufferedSource bufferedSource = Okio.buffer(Okio.source(originalFile));
            byte[] bytes = bufferedSource.readByteArray();
            bufferedSource.close();
            return (T) mConfig.asObject(bytes);
        } catch (IOException e) {
            if (!fast) {
                if (originalFile.exists() && !originalFile.delete()) {
                    throw new RuntimeException("Couldn't clean up broken/unserializable file "
                            + originalFile, e);
                }
            }
            throw new RuntimeException(e);
        }
    }

    public void addOnWriteListener(String key, OnWriteListener onWriteListener) {
        mCallbacks.put(key, onWriteListener);
    }

    public void removeListener(String key) {
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
        final String dbPath = getDbPath(mPath, mName);
        if (!deleteDirectory(dbPath)) {
            System.out.print("Couldn't delete Floppy dir " + dbPath);
        }
    }

    private File getOriginalFile(String key) {
        File file = mFiles.get(key);
        if (file == null) {
            final String tablePath = mFilesDir + File.separator + key + ".pt";
            file = new File(tablePath);
            mFiles.put(key, file);
        }
        return file;
    }

    private String getDbPath(String path, String dbName) {
        return path + File.separator + dbName;
    }

    private String createDiskDir() {
        String filesDir = getDbPath(mPath, mName);
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

    private File makeBackupFile(File originalFile) {
        return new File(originalFile.getPath() + ".bak");
    }
}
