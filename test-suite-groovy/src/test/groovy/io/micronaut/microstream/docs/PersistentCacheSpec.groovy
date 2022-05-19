package io.micronaut.microstream.docs

import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.Specification

class PersistentCacheSpec extends Specification {

    void "backed cache persists over restarts"() {
        given:
        def config = [storageDirectory: "build/microstream-cache-${UUID.randomUUID()}"]
        def server = ApplicationContext.run(EmbeddedServer.class, config, "cachepersist")
        def counter = server.applicationContext.getBean(CounterService)

        when:
        counter.setCount("Tim", 1337L)
        Long count = counter.currentCount("Tim")

        then:
        count == 1337

        when:
        counter.setCount("Tim", 666L);

        and: "we restart the server"
        server.stop()
        server = ApplicationContext.run(EmbeddedServer.class, config, "cachepersist")
        counter = server.applicationContext.getBean(CounterService)

        and: "we get the count"
        count = counter.currentCount("Tim")

        then:
        count == 666
    }
}
