package io.fabianterhorst.floppy;

import org.nustaq.serialization.FSTConfiguration;

import java.util.LinkedHashMap;
import java.util.Map;

public class Floppy {

    private static final String DEFAULT_DB_NAME = "default.disk";

    private static final int DB_TYPE_DEFAULT = 0;

    private static final int DB_TYPE_MEMORY = 1;

    private static final int DB_TYPE_ARRAY = 2;

    private static String mPath;

    //private static final ConcurrentHashMap<String, Disk> mDiskMap = new ConcurrentHashMap<>();

    private static final Map<String, Disk> mDiskMap = new LinkedHashMap<>();

    private static final FSTConfiguration mConfig = FSTConfiguration.createDefaultConfiguration();

    public Floppy() {

    }

    public static void init(String path) {
        mPath = path;
        mConfig.setStructMode(true);
        mConfig.getCLInfoRegistry().setIgnoreAnnotations(true);
    }

    public static void init(String path, Class... classes) {
        init(path);
        registerClasses(classes);
    }

    public static void registerClasses(Class... classes) {
        mConfig.registerClass(classes);
    }

    public static Disk disk(String name) {
        return getDisk(name, DB_TYPE_DEFAULT);
    }

    public static Disk memoryDisk(String name) {
        return getDisk(name, DB_TYPE_MEMORY);
    }

    public static ArrayDisk arrayDisk(String name) {
        return getDisk(name, DB_TYPE_ARRAY);
    }

    public static Disk disk() {
        return getDisk(DEFAULT_DB_NAME, DB_TYPE_DEFAULT);
    }

    public static Disk memoryDisk() {
        return getDisk(DEFAULT_DB_NAME, DB_TYPE_MEMORY);
    }

    public static ArrayDisk arrayDisk() {
        return getDisk(DEFAULT_DB_NAME, DB_TYPE_ARRAY);
    }

    @SuppressWarnings("unchecked")
    private static <T> T getDisk(String name, int type) {
        if (mPath == null) {
            throw new RuntimeException("Floppy.init is not called");
        }
        synchronized (mDiskMap) {
            Disk disk = mDiskMap.get(name);
            if (disk == null) {
                if (type == DB_TYPE_DEFAULT) {
                    disk = new Disk(name, mPath, mConfig);
                } else if (type == DB_TYPE_MEMORY) {
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
