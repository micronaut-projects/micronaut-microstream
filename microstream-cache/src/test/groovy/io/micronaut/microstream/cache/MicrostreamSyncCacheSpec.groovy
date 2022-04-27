package io.micronaut.microstream.cache

import io.micronaut.cache.tck.AbstractSyncCacheSpec
import io.micronaut.context.ApplicationContext
import spock.lang.Shared
import spock.lang.TempDir

class MicrostreamSyncCacheSpec extends AbstractSyncCacheSpec {

    @TempDir
    @Shared
    File tempDir

    @Override
    ApplicationContext createApplicationContext() {
        ApplicationContext.run(
                'microstream.storage.cache.storage-directory': 'build/microstream${random.shortuuid}',
                'microstream.cache.counter.statistics-enabled': "true",
                'microstream.cache.counter2.statistics-enabled': "true",
                'microstream.cache.test.statistics-enabled': "true",
        )
    }
}
