# TreeMap vs ConcurrentSkipListMap Benchmark

Single-threaded JMH microbenchmark comparing `TreeMap` and `ConcurrentSkipListMap` across five operations and three map sizes.

| Benchmark     | What it measures                              |
|---------------|-----------------------------------------------|
| `put`         | Building a fresh map from scratch             |
| `get`         | 1000 random key lookups                       |
| `remove`      | 1000 removes on a copy of the map             |
| `iterate`     | Full `entrySet` traversal                     |
| `floorCeiling`| 1000 `floorKey` + `ceilingKey` calls          |

Parameterized over `size` = 100 / 10 000 / 1 000 000 and both map types → 30 result rows.

## Requirements

- Java 25
- Maven 3.8+

## Compile

```bash
mvn clean package
```

## Run

```bash
java --sun-misc-unsafe-memory-access=allow -jar target/benchmarks.jar \
  -wi 5 -i 10 -r 2s -w 2s -f 2 \
  -jvmArgs "--sun-misc-unsafe-memory-access=allow"
```

| Flag                                    | Meaning                                                        |
|-----------------------------------------|----------------------------------------------------------------|
| `--sun-misc-unsafe-memory-access=allow` | suppresses Java 23+ deprecation warnings from JMH internals    |
| `-wi 5`                                 | 5 warmup iterations per benchmark (lets JIT settle)            |
| `-i 10`                                 | 10 measurement iterations per benchmark                        |
| `-r 2s`                                 | each measurement iteration runs for 2 seconds                  |
| `-w 2s`                                 | each warmup iteration runs for 2 seconds                       |
| `-f 2`                                  | 2 JVM forks — isolates JIT state between benchmarks            |
| `-jvmArgs`                              | passes the unsafe flag into each forked benchmark JVM as well  |

Expected total runtime: ~60 minutes.

### Quick smoke run (~5 minutes)

```bash
java --sun-misc-unsafe-memory-access=allow -jar target/benchmarks.jar \
  -wi 1 -i 3 -r 1s -w 1s -f 1 \
  -jvmArgs "--sun-misc-unsafe-memory-access=allow"
```

### Run a single benchmark

```bash
java --sun-misc-unsafe-memory-access=allow -jar target/benchmarks.jar ".*get.*" \
  -wi 5 -i 10 -r 2s -w 2s -f 2 \
  -jvmArgs "--sun-misc-unsafe-memory-access=allow"
```

### Run a single map size

```bash
java --sun-misc-unsafe-memory-access=allow -jar target/benchmarks.jar \
  -p size=10000 -wi 5 -i 10 -r 2s -w 2s -f 2 \
  -jvmArgs "--sun-misc-unsafe-memory-access=allow"
```
