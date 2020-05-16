/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.benchmarks;

import org.ufoss.dino.bench.DirectMemoryReadBench;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 1)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class DirectMemoryReadBenchmark {

    DirectMemoryReadBench parent = new DirectMemoryReadBench();

    @Setup
    public void setup() {
        parent.setup();
    }

    @TearDown
    public void tearDown() {
        parent.tearDown();
    }

    @Benchmark
    public void directRead() {
        parent.directRead();
    }

    @Benchmark
    public void indexedRead() {
        parent.indexedRead();
    }

    @Benchmark
    public void varhandleRead() {
        parent.varhandleRead();
    }

    @Benchmark
    public void indexedReadMemorySegmentAssociated() {
        parent.indexedReadMemorySegmentAssociated();
    }

    @Benchmark
    public void varhandleMemorySegmentReadGroupAndStruct() {
        parent.varhandleMemorySegmentReadGroupAndStruct();
    }
}
