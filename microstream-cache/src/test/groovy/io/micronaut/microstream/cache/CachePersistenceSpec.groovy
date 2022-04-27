package io.micronaut.microstream.cache

import io.micronaut.cache.AsyncCache
import io.micronaut.cache.SyncCache
import io.micronaut.context.ApplicationContext
import io.micronaut.core.type.Argument
import io.micronaut.inject.qualifiers.Qualifiers
import spock.lang.Specification

class CachePersistenceSpec extends Specification {

    void "cache remains over restarts"() {
        given:
        Map<String, Object> properties = [
                "microstream.storage.cachestore.storage-directory": "build/microstream-${UUID.randomUUID().toString()}",
                "microstream.cache.backed.key-type"               : "java.lang.Integer",
                "microstream.cache.backed.value-type"             : "java.lang.String",
                "microstream.cache.backed.statistics-enabled"     : "true",
                "microstream.cache.backed.backing-storage"        : "cachestore",
        ]

        ApplicationContext applicationContext = ApplicationContext.run(properties)

        when: 'we cache some data in the backed cache'
        SyncCache<?> backedCache = applicationContext.getBean(SyncCache.class, Qualifiers.byName("backed"))
        backedCache.put(1, "This is some data to cache")

        then:
        backedCache.get(1, Argument.of(String)).get() == "This is some data to cache"

        when: 'we restart the app with the same backing data store'
        applicationContext.stop()
        applicationContext = ApplicationContext.run(properties)
        backedCache = applicationContext.getBean(SyncCache.class, Qualifiers.byName("backed"))

        then: 'the data is still there in the backed cache'
        backedCache.get(1, Argument.of(String)).get() == "This is some data to cache"

        cleanup:
        applicationContext.stop()
    }

    void "async cache remains over restarts"() {
        given:
        Map<String, String> properties = [
                "microstream.storage.cachestore.storage-directory": "build/microstream-${UUID.randomUUID().toString()}",

                "microstream.cache.backed.key-type"               : "java.lang.Integer",
                "microstream.cache.backed.value-type"             : "java.lang.String",
                "microstream.cache.backed.statistics-enabled"     : "true",
                "microstream.cache.backed.backing-storage"        : "cachestore",
        ]

        ApplicationContext applicationContext = ApplicationContext.run(properties)

        when: 'we cache some data in the backed cache'
        AsyncCache<?> backedCache = applicationContext.getBean(SyncCache, Qualifiers.byName("backed")).async()
        backedCache.put(1, "This is some data to cache").get()

        then:
        backedCache.get(1, Argument.of(String)).get().get() == "This is some data to cache"

        when: 'we restart the app with the same backing data store'
        applicationContext.stop()
        applicationContext = ApplicationContext.run(properties)
        backedCache = applicationContext.getBean(SyncCache, Qualifiers.byName("backed")).async()

        then: 'the data is still there in the backed cache'
        backedCache.get(1, Argument.of(String)).get().get() == "This is some data to cache"

        cleanup:
        applicationContext.stop()
    }
}
