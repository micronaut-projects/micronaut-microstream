package io.micronaut.microstream.docs

import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.Specification

class CacheSpec extends Specification {

    void 'cache works as expected'() {
        given:
        def config = [storageDirectory: "build/microstream-cache-${UUID.randomUUID()}"]
        def server = ApplicationContext.run(EmbeddedServer.class, config, "cache")

        CounterService counter = server.applicationContext.getBean(CounterService)

        when:
        counter.setCount("Tim", 1337)
        Long count = counter.currentCount("Tim")

        then:
        count == 1337

        when: "Change the store so we check we're seeing a cached value"
        counter.counters.Tim = -1
        count = counter.currentCount("Tim")

        then:
        count == 1337

        cleanup:
        server.stop()
    }
}
