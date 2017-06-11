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
        folder.newFolder("default.disk");
        folder.newFile("default.disk/test.pt");
        disk = Floppy.disk();
    }

    @Test
    public void testReadWriteValue() {
        String value = "Hello";
        Assert.assertEquals(null, disk.read("test"));
        disk.write("test", value);
        Assert.assertEquals(value, disk.read("test"));
    }
}
