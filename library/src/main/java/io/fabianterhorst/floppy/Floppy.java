package io.fabianterhorst.floppy;

import java.util.concurrent.ConcurrentHashMap;

public class Floppy {

    private static final String DEFAULT_DB_NAME = "default.disk";

    private static final String DB_TYPE_MEMORY = "memory";

    private static String mPath;

    private static final ConcurrentHashMap<String, Disk> mDiskMap = new ConcurrentHashMap<>();

    public Floppy() {

    }

    public static void init(String path) {
        mPath = path;
    }

    private static Disk disk(String name, String type) {
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

    public static Disk disk() {
        return getDisk(DEFAULT_DB_NAME, null);
    }

    public static Disk memoryDisk() {
        return getDisk(DEFAULT_DB_NAME, DB_TYPE_MEMORY);
    }

    private static Disk getDisk(String name, String type) {
        if (mPath == null) {
            throw new RuntimeException("Floppy.init is not called");
        }
        synchronized (mDiskMap) {
            Disk disk = mDiskMap.get(name);
            if (disk == null) {
                if (type == null) {
                    disk = new Disk(name, mPath);
                } else {
                    disk = new MemoryDisk(name, mPath);
                }
                mDiskMap.put(name, disk);
            }
            return disk;
        }
    }
}
