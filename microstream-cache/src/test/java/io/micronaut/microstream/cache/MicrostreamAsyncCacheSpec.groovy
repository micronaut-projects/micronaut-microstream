package io.micronaut.microstream.cache

import io.micronaut.cache.tck.AbstractAsyncCacheSpec
import io.micronaut.context.ApplicationContext
import spock.lang.Shared
import spock.lang.TempDir

class MicrostreamAsyncCacheSpec extends AbstractAsyncCacheSpec {

    @TempDir
    @Shared
    File tempDir

    @Override
    ApplicationContext createApplicationContext() {
        ApplicationContext.run(
                'microstream.cache.counter.statistics-enabled': "true",
                'microstream.cache.counter2.statistics-enabled': "true",
                'microstream.cache.test.statistics-enabled': "true",
        )
    }
}
