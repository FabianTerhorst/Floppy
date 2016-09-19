package io.fabianterhorst.floppy.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.fabianterhorst.floppy.Disk;
import io.fabianterhorst.floppy.Floppy;
import io.fabianterhorst.floppy.OnWriteListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Floppy.init(getFilesDir().toString());

        Log.d("path", getFilesDir().toString());

        for(File file : getFilesDir().listFiles()) {
            Log.d("path", file.getName());
        }

        Disk disk = Floppy.memoryDisk();
        List<Integer> integers = new ArrayList<>();
        for (int i = 0;i < 500; i++) {
            integers.add(500);
        }
        long ts = System.currentTimeMillis();
        disk.write("bla123", integers);
        Log.d("time write", String.valueOf(System.currentTimeMillis() - ts));
        disk.write("bla", "bla2");
        disk.write("bla", "bla3");
        ts = System.currentTimeMillis();
        Log.d("value", disk.<String>read("bla"));
        Log.d("time read", String.valueOf(System.currentTimeMillis() - ts));

        disk.setOnWriteListener("bla", new OnWriteListener<String>() {
            @Override
            public void onWrite(String value) {
                Log.d("valueChange", value);
            }
        });
        disk.write("bla", "bla4");
        disk.write("bla2", "bla5");
        disk.write("bla", "bla6");

        //for cache Floppy.memoryDisk()
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Floppy.disk().removeListener("bla");
    }
}
