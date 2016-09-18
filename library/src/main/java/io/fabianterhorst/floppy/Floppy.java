package io.fabianterhorst.floppy;

import java.util.concurrent.ConcurrentHashMap;

public class Floppy {

	private static final String DEFAULT_DB_NAME = "default.disk";

	private static String mPath;

	private static final ConcurrentHashMap<String, Disk> mDiskMap = new ConcurrentHashMap<>();

	public Floppy() {

	}

	public static void init(String path) {
		mPath = path;
	}

	public static Disk disk(String name) {
		if (name.equals(DEFAULT_DB_NAME)) throw new RuntimeException(DEFAULT_DB_NAME +
				" name is reserved for default library name");
		return getDisk(name);
	}

	public static Disk disk() {
		return getDisk(DEFAULT_DB_NAME);
	}

	private static Disk getDisk(String name) {
		if (mPath == null) {
			throw new RuntimeException("Floppy.init is not called");
		}
		synchronized (mDiskMap) {
			Disk disk = mDiskMap.get(name);
			if (disk == null) {
				disk = new Disk(name, mPath);
				mDiskMap.put(name, disk);
			}
			return disk;
		}
	}
}
