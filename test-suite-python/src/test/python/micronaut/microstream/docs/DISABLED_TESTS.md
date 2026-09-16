# Python Docs Disabled Test Inventory

This file tracks Python docs examples of Micronaut MicroStream that are present but disabled, or that deviate from the
Java example because the direct port currently fails compilation or at runtime (Python compiler gaps). Use it as the
bug-fixing task list for the final migration wave.

## Reconciliation

- Last generated active `@Disabled` count: 2.
- Last generated command: `rg -n "@Disabled\(" test-suite-python/src/test/python`.
- Last full-suite command: `./gradlew :test-suite-python:test -Ppython-ci`.
- Last full-suite result: build successful, 3 tests executed (3 test classes), 2 skipped.

## Migration Rules

- Methods that implement a Java interface keep the Java (camelCase) name; other methods and constructor parameters are
  snake_case (`find_by_id`, `root_provider`). Model attributes whose names are JSON keys keep the Java name (`firstName`).
- Models are `@dataclass` classes (`Customer`, `CustomerSave`, `Data`), the MicroStream root instance uses
  `field(default_factory=dict)` for its map like the Kotlin data class.
- The Java `TaskExecutors.BLOCKING` / `HttpStatus.NO_CONTENT` constants are used as annotation members the same way.
- `XThreads.executeSynchronized(...)` receives a nested Python function; `Optional<Customer>` is `Customer | None`.
- Imported shim classes of Java types cannot be used as runtime type arguments (`TypeError: invalid instantiation of
  foreign object`): `ApplicationContext.run(EmbeddedServer, ...)`, `createBean(HttpClient, url)`, `getBean(CounterService)`
  and `client.exchange(request, Customer)` use `java.type(...)` aliases (marked `# TODO(python)`); Python beans returned by
  `getBean` are unwrapped with `.asPolyglotValue()`.
- `CacheTest` is a `@MicronautTest(environments=["cache"])` with the storage directory supplied through `@Property`
  instead of a manual `ApplicationContext.run(...)`; `PersistentCacheTest` has to restart the application and keeps the
  nested `ApplicationContext.run(EmbeddedServer, config, "cachepersist")` calls of the Java test.
- This branch builds on Micronaut 4.9 (Java 17 baseline) while the Python compiler and runtime ship with Micronaut core
  5.2+ (Java 25): only `test-suite-python` resolves the Micronaut 5 versions of core, cache, serde, validation and test
  (`micronautBuild.python.compilerVersion` and the `*-python` entries of `gradle/libs.versions.toml`) and targets Java 25.

## Active `@Disabled` Tests

| Test | Reason |
| --- | --- |
| `PersistentCacheTest.cache_persists_over_restarts` | Not a Python compiler gap: MicroStream 08.01.02 calls `sun.misc.Unsafe.ensureClassInitialized`, which was removed in JDK 22, so creating a `StorageManager` fails with `NoSuchMethodError: 'void sun.misc.Unsafe.ensureClassInitialized(java.lang.Class)'` on the JDK 25 the Python runtime requires (the Java, Kotlin and Groovy storage tests of this branch fail the same way on JDK 25). |
| `CustomerControllerTest.test_crud` | MicroStream cannot persist the classes generated for Python classes (observed with the identical EclipseStore port, micronaut-projects/micronaut-eclipsestore#329; on this branch the `StorageManager` cannot even be created on JDK 25, see above). With the `@dataclass` root, `StorageManagerFactory` fails first with `InstantiationException: Could not instantiate type [micronaut.microstream.docs.Data]: micronaut.microstream.docs.Data.<init>()` (the generated class has no no-arg constructor although every field has a default; with `@Introspected` the message is `No default constructor exists`). A plain class with `def __init__(self)` gets a no-arg constructor, but then `storeRoot()` fails with `PersistenceExceptionTypeNotPersistable: Type not persistable: "class com.oracle.graal.python.builtins.objects.cext.capi.transitions.CApiTransitions$PythonObjectReference"` because the generated class keeps the GraalPy `Value` in a non-transient field; where the store call happens to succeed the data is silently lost on restart (the persisted graph is the runtime handle, and the Java-side copies of an `@Introspected` class are re-synchronised from Java to Python on every `asPolyglotValue()` call, so a `@Introspected` root with a no-arg `__init__` fails with `TypeError: 'null' object does not support item assignment` because the Python defaults are not applied by the no-arg constructor). The test is a faithful port (two repository implementations, four server restarts) and runs as soon as Python objects can be stored. |

## Commented Unsupported Snippet Ports

None.

## Intentionally Unsupported Snippet Targets

None.
