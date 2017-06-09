package io.fabianterhorst.floppy.sample;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.fabianterhorst.floppy.ArrayDisk;
import io.fabianterhorst.floppy.Floppy;
import io.fabianterhorst.floppy.OnChangeListener;
import io.fabianterhorst.floppy.OnEqualListener;
import io.fabianterhorst.floppy.OnFindListener;
import io.fabianterhorst.floppy.OnReadListener;
import io.fabianterhorst.floppy.OnWriteListener;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class MainActivity extends AppCompatActivity {

    ArrayDisk disk = Floppy.arrayDisk();

    ArrayList<User> items = disk.read("items2");

    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            FileOutputStream fo = new FileOutputStream(getFilesDir() + File.separator + "bla.txt");
            long time = System.nanoTime();
            ObjectOutputStream so = new ObjectOutputStream(fo);
            User user = new User();
            user.setName("bla");
            user.writeExternal(so);
            so.flush();

            Log.d("time", String.valueOf(System.nanoTime() - time));

            BufferedSink sink = Okio.buffer(Okio.sink(new File(getFilesDir() + File.separator + "bla.txt")));
            time = System.nanoTime();
            so = new ObjectOutputStream(sink.outputStream());
            ArrayList<User> users = new ArrayList<>();
            user = new User();
            user.setName("bla");
            users.add(user);
            User user2 = new User();
            user2.setName("bla");
            users.add(user2);
            for (User currentUser : users) {
                currentUser.writeExternal(so);
            }
            //user.writeExternal(so);
            so.flush();

            Log.d("time", String.valueOf(System.nanoTime() - time));

            BufferedSource source = Okio.buffer(Okio.source(new File(getFilesDir() + File.separator + "bla.txt")));
            ObjectInputStream inputStream = new ObjectInputStream(source.inputStream());
            User newUser;
            try {
                while (inputStream.available() > 0) {
                    newUser = new User();
                    newUser.readExternal(inputStream);
                    Log.d("user", newUser.getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            /*FSTConfiguration fstConfiguration = FSTConfiguration.createDefaultConfiguration();

            time = System.nanoTime();
            so = new ObjectOutputStream(fo);
            user = new User();
            user.setName("bla");
            so.write(fstConfiguration.asByteArray(user));
            so.flush();
            Log.d("time2", String.valueOf(System.nanoTime() - time));
            */
        }catch (Exception e) {
            e.printStackTrace();
        }

        try {
            int oldVersion = disk.read("version", 0);
            int newVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            if (oldVersion < newVersion) {
                for (int i = oldVersion; i < newVersion; i++) {
                    if (i == 12) {
                        //migrate from 12 to 13
                        //disk.delete("items");
                    }
                    //migrate with all version codes
                }
                disk.write("version", newVersion);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        long time = System.nanoTime();
        User saveUser = new User();
        saveUser.setName("bla123");
        disk.write("user", saveUser);
        Log.d("time write", String.valueOf(System.nanoTime() - time));
        time = System.nanoTime();
        //User newUser = new User();
        User newUser = disk.read("user");
        Log.d("name", newUser.getName());
        Log.d("time read", String.valueOf(System.nanoTime() - time));


        disk.addOnEqualListener("items", new OnEqualListener<User>() {
            @Override
            public boolean isEqual(User currentUser, User newUser) {
                return currentUser.getName().equals(newUser.getName());
            }
        });

        disk.addOnChangeListener("items", (OnChangeListener<User>) (user, index) -> {

        });

        disk.changeItem("items",
                (OnFindListener<User>) user -> {
                    return user.getName().equals("fabian");
                },
                (OnReadListener<User>) user -> {
                    user.setName("fabian");
                    return user;
                }
        );

        layout = (LinearLayout) findViewById(R.id.items);

        if (items == null) {
            items = new ArrayList<>();
            disk.write("items2", items);
        }

        refresh();

        //for cache Floppy.memoryDisk()
        final List<PersonArg> contacts = TestDataGenerator.genPersonArgList(500);
        long floppyTime = runTest(new FloppyReadWriteContactsArgTest(), contacts, 30); //Prepare
        printResults("Write 500 contacts", floppyTime);

        Button button = (Button) findViewById(R.id.add);

        button.setOnClickListener(view -> {
            User user = new User();
            user.setName("i" + String.valueOf(items.size() + 1));
            disk.addItem("items2", user);
            //disk.removeItem("items", 0);
        });

        disk.addOnWriteListener("items2", new OnWriteListener<ArrayList<User>>() {
            @Override
            public void onWrite(ArrayList<User> items) {
                refresh();
            }
        });

    }

    private void refresh() {
        layout.removeAllViews();
        for (User item : items) {
            TextView textView = new TextView(this);
            textView.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setText(item.getName());
            layout.addView(textView);
        }
    }

    private void printResults(String name, long ironTime) {
        Log.i("benchmark", String.format("..................................\n%s \n Floppy: %d",
                name, ironTime));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Floppy.disk().removeListener("items");
    }

    private <T> long runTest(TestTask<T> task, T extra, int repeat) {
        long start = SystemClock.uptimeMillis();
        for (int i = 0; i < repeat; i++) {
            task.run(i, extra);
        }
        return (SystemClock.uptimeMillis() - start) / repeat;
    }

    interface TestTask<T> {
        void run(int i, T extra);
    }

    private class FloppyReadWriteContactsArgTest implements TestTask<List<PersonArg>> {
        @Override
        public void run(int i, List<PersonArg> extra) {
            String key = "contacts" + i;
            Floppy.disk("test2").write(key, extra);
            Floppy.disk("test2").read(key);
        }
    }
}
