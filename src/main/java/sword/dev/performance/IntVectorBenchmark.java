package sword.dev.performance;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import sword.dev.BufferAllocator;
import sword.dev.IntVectorV2;
import sword.dev.RootAllocator;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
@Fork(value = 2, jvmArgs = {"-XX:+UnlockExperimentalVMOptions", "-XX:+EnableVectorSupport", "--add-modules=jdk.incubator.vector"})
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 2, time = 1)
public class IntVectorBenchmark {

    private IntVectorV2 vector;
    private int[] values;

    @Setup
    public void setup() {
        BufferAllocator allocator = new RootAllocator();
        vector = new IntVectorV2("test", allocator);
        vector.allocateNew();
        values = new int[10000000];
        for (int i = 0; i < values.length; i++) {
            values[i] = i;
        }
    }

    @Benchmark
    public void setBenchmark() {
        for (int i = 0; i < values.length; i++) {
            vector.set(i, values[i]);
        }
    }

    @Benchmark
    public void setSimdBenchmark() {
        vector.setSimd(0, values);
    }

    /*
    @Benchmark
    public void getBenchmark() {
        for (int i = 0; i < values.length; i++) {
            values[i] = vector.get(i);
        }
    }

    @Benchmark
    public void getSimdBenchmark() {
        vector.getSimd(0, values);
    }
    */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(IntVectorBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}
