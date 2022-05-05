package io.micronaut.microstream.cache

import io.micronaut.cache.tck.AbstractAsyncCacheSpec
import io.micronaut.context.ApplicationContext
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.TempDir

class MicrostreamAsyncCacheSpec extends AbstractAsyncCacheSpec {

    @TempDir
    @Shared
    File tempDir

    @AutoCleanup
    ApplicationContext applicationContext

    @Override
    ApplicationContext createApplicationContext() {
        this.applicationContext = ApplicationContext.run(
                'microstream.cache.counter.statistics-enabled': "true",
                'microstream.cache.counter2.statistics-enabled': "true",
                'microstream.cache.test.statistics-enabled': "true",
        )
    }
}
