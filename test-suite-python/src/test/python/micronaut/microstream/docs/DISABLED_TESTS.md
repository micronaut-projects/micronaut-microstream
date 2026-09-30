# Python Docs Disabled Test Inventory

This file tracks Python docs examples of Micronaut MicroStream that are present but disabled, or that deviate from the
Java example because the direct port currently fails compilation or at runtime (Python compiler gaps). Use it as the
bug-fixing task list for the final migration wave.

## Reconciliation

- Last generated active `@Disabled` count: 2.
- Last generated command: `rg -n "@Disabled\(" test-suite-python/src/test/python`.
- Last full-suite command: `./gradlew :test-suite-python:test -Ppython-ci`.
- Last full-suite result: build successful, 3 tests executed (3 test classes), 2 skipped (core 5.2.9 /
  micronaut-build 8.1.3 for the Python suite).

## Migration Rules

- Methods that implement a Java interface keep the Java (camelCase) name; other methods and constructor parameters are
  snake_case (`find_by_id`, `root_provider`). Model attributes whose names are JSON keys keep the Java name (`firstName`).
- Models are `@dataclass` classes (`Customer`, `CustomerSave`, `Data`), the MicroStream root instance uses
  `field(default_factory=dict)` for its map like the Kotlin data class.
- The Java `TaskExecutors.BLOCKING` / `HttpStatus.NO_CONTENT` constants are used as annotation members the same way.
- `XThreads.executeSynchronized(...)` receives a nested Python function; `Optional<Customer>` is `Customer | None`.
- Imported classes are used as runtime type arguments (`ApplicationContext.run(EmbeddedServer, ...)`, `createBean(HttpClient, url)`,
  `getBean(CounterService)`, `client.exchange(request, Customer)`); `getBean` of a Python class returns the Python object.
- The root instance is `@Introspected` so that the generated Java class is persistable (only its property fields are
  persistent, the GraalPy object is transient).
- `CacheTest` is a `@MicronautTest(environments=["cache"])` instead of the manual `ApplicationContext.run(...)` of the
  Java, Kotlin and Groovy tests. The `cache` environment has no storage backing, so the unique storage directory those
  tests pass is not needed (an annotation member has to be a compile-time constant and cannot hold a fresh UUID).
  `PersistentCacheTest` does restart the application and keeps the nested
  `ApplicationContext.run(EmbeddedServer, config, "cachepersist")` calls of the Java test.
- This branch builds on Micronaut 4.9 (Java 17 baseline, Java 21 toolchain) while the Python compiler and runtime ship
  with Micronaut core 5.2+ (Java 25): only `test-suite-python` resolves the Micronaut 5 versions of core, cache, serde,
  validation and test (`micronautBuild.python.compilerVersion` and the `*-python` entries of
  `gradle/libs.versions.toml`) and overrides the toolchain to 25.

## Active `@Disabled` Tests

| Test | Reason |
| --- | --- |
| `PersistentCacheTest.cache_persists_over_restarts` | Not a Python compiler gap: MicroStream 08.01.02 calls `sun.misc.Unsafe.ensureClassInitialized`, which was removed in JDK 22, so creating a `StorageManager` fails on the JDK 25 the Python runtime requires. Re-checked on core 5.2.9: `BeanInstantiationException: Error instantiating bean of type [one.microstream.storage.types.StorageManager] … Caused by: java.lang.NoSuchMethodError: 'void sun.misc.Unsafe.ensureClassInitialized(java.lang.Class)'`. The rest of this branch now builds and tests with a Java 21 toolchain, where the Java, Kotlin and Groovy ports of this test pass; only the Python suite has to run on JDK 25. |
| `CustomerControllerTest.test_crud` | Same root cause: the `StorageManager` cannot be created on JDK 25, so every request fails. Re-checked on core 5.2.9: `HttpClientResponseException: Internal Server Error`, `Caused by: java.lang.NoSuchMethodError: 'void sun.misc.Unsafe.ensureClassInitialized(java.lang.Class)'`. Beyond that, and not observable here, the identical EclipseStore port (micronaut-projects/micronaut-eclipsestore#329) showed that the store instantiates the root class reflectively through the generated no-arg constructor, which for an all-default `@dataclass` creates the object *in Python* so that the dataclass defaults apply; the root is then Python-owned and Python code that receives it as a Java object (`StorageManager.root()`, `RootProvider.root()`) gets a converted copy of `customers` on every access. Objects created through the field-assigning constructors of the generated class are Java-owned and persist as documented. The test is a faithful port and runs as soon as MicroStream works on JDK 25 (and, if it then resurfaces, the root ownership is fixed). |

## Commented Unsupported Snippet Ports

None.

## Intentionally Unsupported Snippet Targets

None.
