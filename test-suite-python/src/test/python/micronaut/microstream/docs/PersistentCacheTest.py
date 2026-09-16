import uuid

import java
from micronaut.context import ApplicationContext
from micronaut.runtime.server import EmbeddedServer
from micronaut.test.extensions.junit5.annotation import MicronautTest
from org.junit.jupiter.api import Disabled, Test

# TODO(python): the imported shim classes cannot be used as runtime type arguments of ApplicationContext.run /
# getBean ("TypeError: invalid instantiation of foreign object"), only java.type(...) aliases can
EmbeddedServerType = java.type("io.micronaut.runtime.server.EmbeddedServer")
CounterServiceType = java.type("micronaut.microstream.docs.CounterService")


@MicronautTest
class PersistentCacheTest:

    # MicroStream 08.01.02 calls sun.misc.Unsafe.ensureClassInitialized, which was removed in JDK 22, while the Python
    # runtime (Micronaut core 5.2) requires JDK 25: creating the StorageManager fails with a NoSuchMethodError
    @Disabled("MicroStream 08.01.02 does not run on JDK 22+ (sun.misc.Unsafe.ensureClassInitialized) and the Python runtime requires JDK 25")
    @Test
    def cache_persists_over_restarts(self) -> None:
        config = {"storageDirectory": "build/microstream-cache-" + str(uuid.uuid4())}
        # When we create the app, and use a cached method
        server: EmbeddedServer = ApplicationContext.run(EmbeddedServerType, config, "cachepersist")
        try:
            counter = server.getApplicationContext().getBean(CounterServiceType).asPolyglotValue()
            counter.set_count("Tim", 1337)
            count = counter.current_count("Tim")
            assert count == 1337
            counter.set_count("Tim", 666)
        finally:
            server.close()

        # Then restarting the app with the same storage location, the value is still cached
        server = ApplicationContext.run(EmbeddedServerType, config, "cachepersist")
        try:
            counter = server.getApplicationContext().getBean(CounterServiceType).asPolyglotValue()
            count = counter.current_count("Tim")
            assert count == 666
        finally:
            server.close()
