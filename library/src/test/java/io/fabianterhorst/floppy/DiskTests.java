package io.fabianterhorst.floppy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

/**
 * Created by fabianterhorst on 11.06.17.
 */

public class DiskTests {

    Disk disk;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void init() throws IOException {
        File currentFolder = folder.getRoot();
        Floppy.init(currentFolder.getPath());
        disk = Floppy.disk();
    }

    /*@Test
    public void testDisk() throws IOException {
        folder.newFolder("1.disk");
        folder.newFolder("2.disk");
        Assert.assertFalse(Floppy.disk("1.disk") instanceof MemoryDisk);
        Assert.assertFalse(Floppy.disk("1.disk") instanceof ArrayDisk);
        Assert.assertTrue(Floppy.memoryDisk("2.disk") instanceof MemoryDisk);
        Assert.assertFalse(Floppy.memoryDisk("2.disk") instanceof ArrayDisk);
    }*/

    @Test
    public void testReadWriteValue() throws IOException {
        File defaultFolder = new File(folder.getRoot(), "default.disk");
        defaultFolder.mkdir();
        new File(defaultFolder, "test.pt");
        String value = "Hello";
        Assert.assertEquals(null, disk.read("test"));
        disk.write("test", value);
        Assert.assertEquals(value, disk.read("test"));
    }
}
