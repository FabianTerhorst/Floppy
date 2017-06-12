package io.fabianterhorst.floppy.sample;

import android.support.test.InstrumentationRegistry;

import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;

import dk.ilios.spanner.BeforeExperiment;
import dk.ilios.spanner.Benchmark;
import dk.ilios.spanner.BenchmarkConfiguration;
import dk.ilios.spanner.SpannerConfig;
import dk.ilios.spanner.config.RuntimeInstrumentConfig;
import dk.ilios.spanner.junit.SpannerRunner;
import io.fabianterhorst.floppy.Disk;
import io.fabianterhorst.floppy.Floppy;

/**
 * Created by fabianterhorst on 12.06.17.
 */

@RunWith(SpannerRunner.class)
public class FloppyBenchmark {

    private File filesDir = InstrumentationRegistry.getTargetContext().getFilesDir();
    private File resultsDir = new File(filesDir, "results");

    @BenchmarkConfiguration
    public SpannerConfig configuration = new SpannerConfig.Builder()
            //.saveResults(resultsDir, FloppyBenchmark.class.getCanonicalName() + ".json") // Save results to disk
            //.medianFailureLimit(Float.MAX_VALUE) // Fail if difference vs. baseline is to big. Should normally be 10-15%  (0.15)
            //.addInstrument(RuntimeInstrumentConfig.unittestConfig()) // Configure how benchmark is run/measured
            .addInstrument(RuntimeInstrumentConfig.defaultConfig())
            .maxBenchmarkThreads(1)
            .build();

    private Disk disk;

    private final List<Person> contacts = TestDataGenerator.genPersonList(500);

    @BeforeExperiment
    public void before() {
        Floppy.init(filesDir.toString(), Person.class);
        disk = Floppy.disk();
        disk.deleteAll();
        File defaultFolder = new File(filesDir, "default.disk");
        defaultFolder.mkdir();
        new File(defaultFolder, "users.pt");
        disk.write("contacts", contacts);
    }

    @Benchmark
    public void write500Contacts() {
        disk.write("contacts", contacts);
    }

    @Benchmark
    public void read500Contacts() {
        disk.read("contacts");
    }
}
