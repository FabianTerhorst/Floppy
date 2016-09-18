package io.fabianterhorst.floppy.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.fabianterhorst.floppy.Disk;
import io.fabianterhorst.floppy.Floppy;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Floppy.init(getFilesDir().toString());

		Disk disk = Floppy.disk();

		disk.write("bla", "bla2");

		Log.d("value", disk.<String>read("bla"));
	}
}
