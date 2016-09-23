package io.fabianterhorst.floppy;

import org.nustaq.serialization.FSTConfiguration;

import java.util.concurrent.ConcurrentHashMap;

public class Floppy {

    private static final String DEFAULT_DB_NAME = "default.disk";

    private static final String DB_TYPE_MEMORY = "memory";

    private static final String DB_TYPE_ARRAY = "array";

    private static String mPath;

    private static final ConcurrentHashMap<String, Disk> mDiskMap = new ConcurrentHashMap<>();

    private static final FSTConfiguration mConfig = FSTConfiguration.createDefaultConfiguration();

    public Floppy() {

    }

    public static void init(String path) {
        mPath = path;
        mConfig.setStructMode(true);
        mConfig.getCLInfoRegistry().setIgnoreAnnotations(true);
        mConfig.setPreferSpeed(true);
    }

    public static void init(String path, Class... classes) {
        init(path);
        registerClasses(classes);
    }

    public static void registerClasses(Class... classes) {
        mConfig.registerClass(classes);
    }

    private static <T> T disk(String name, String type) {
        if (name.equals(DEFAULT_DB_NAME)) throw new RuntimeException(DEFAULT_DB_NAME +
                " name is reserved for default library name");
        return getDisk(name, type);
    }

    public static Disk disk(String name) {
        return disk(name, null);
    }

    public static Disk memoryDisk(String name) {
        return disk(name, DB_TYPE_MEMORY);
    }

    public static ArrayDisk arrayDisk(String name) {
        return disk(name, DB_TYPE_ARRAY);
    }

    public static Disk disk() {
        return getDisk(DEFAULT_DB_NAME, null);
    }

    public static Disk memoryDisk() {
        return getDisk(DEFAULT_DB_NAME, DB_TYPE_MEMORY);
    }

    public static ArrayDisk arrayDisk() {
        return getDisk(DEFAULT_DB_NAME, DB_TYPE_ARRAY);
    }

    @SuppressWarnings("unchecked")
    private static <T> T getDisk(String name, String type) {
        if (mPath == null) {
            throw new RuntimeException("Floppy.init is not called");
        }
        synchronized (mDiskMap) {
            Disk disk = mDiskMap.get(name);
            if (disk == null) {
                if (type == null) {
                    disk = new Disk(name, mPath, mConfig);
                } else if (type.equals(DB_TYPE_MEMORY)){
                    disk = new MemoryDisk(name, mPath, mConfig);
                } else {
                    disk = new ArrayDisk(name, mPath, mConfig);
                }
                mDiskMap.put(name, disk);
            }
            return (T) disk;
        }
    }
}
