package com.experiment.bench;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
public class SortedMapBenchmark {

    @Param({"100", "10000", "1000000"})
    int size;

    @Param({"treemap", "skiplist"})
    String mapType;

    private NavigableMap<Integer, Integer> map;
    private int[] keys;
    private int[] lookupKeys;
    private static final int LOOKUP_COUNT = 1000;

    @Setup(Level.Trial)
    public void setup() {
        map = mapType.equals("treemap") ? new TreeMap<>() : new ConcurrentSkipListMap<>();

        Random rng = new Random(42);
        keys = new int[size];
        for (int i = 0; i < size; i++) {
            keys[i] = rng.nextInt();
            map.put(keys[i], i);
        }

        lookupKeys = new int[LOOKUP_COUNT];
        for (int i = 0; i < LOOKUP_COUNT; i++) {
            lookupKeys[i] = keys[rng.nextInt(size)];
        }
    }

    @Benchmark
    public void put(Blackhole bh) {
        NavigableMap<Integer, Integer> m = mapType.equals("treemap") ? new TreeMap<>() : new ConcurrentSkipListMap<>();
        for (int i = 0; i < keys.length; i++) {
            m.put(keys[i], i);
        }
        bh.consume(m);
    }

    @Benchmark
    public void get(Blackhole bh) {
        for (int key : lookupKeys) {
            bh.consume(map.get(key));
        }
    }

    @Benchmark
    public void remove() {
        NavigableMap<Integer, Integer> m = mapType.equals("treemap") ? new TreeMap<>(map) : new ConcurrentSkipListMap<>(map);
        for (int key : lookupKeys) {
            m.remove(key);
        }
    }

    @Benchmark
    public void iterate(Blackhole bh) {
        for (var entry : map.entrySet()) {
            bh.consume(entry.getValue());
        }
    }

    @Benchmark
    public void floorCeiling(Blackhole bh) {
        for (int key : lookupKeys) {
            bh.consume(map.floorKey(key));
            bh.consume(map.ceilingKey(key));
        }
    }
}
