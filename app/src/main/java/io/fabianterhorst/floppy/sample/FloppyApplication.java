package io.fabianterhorst.floppy.sample;

import android.app.Application;
import android.util.Log;

import io.fabianterhorst.floppy.Floppy;

/**
 * Created by fabianterhorst on 21.09.16.
 */

public class FloppyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        long time = System.nanoTime();
        Floppy.init(String.valueOf(getFilesDir()), User.class, Person.class, PersonArg.class);
        Log.d("load time", String.valueOf(System.nanoTime() - time));
    }
}
