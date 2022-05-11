package io.micronaut.microstream.docs

import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.server.EmbeddedServer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID

class PersistentCacheTest {

    @Test
    fun cachePersistsOverRestarts() {
        val config = mapOf("storageDirectory" to "build/microstream-cache-${UUID.randomUUID()}")
        ApplicationContext.run(EmbeddedServer::class.java, config, "cachepersist").use {
            val counter = it.applicationContext.getBean(CounterService::class.java)
            counter.setCount("Tim", 1337)
            val count = counter.currentCount("Tim")
            assertEquals(1337, count)
            counter.setCount("Tim", 666)
        }
        ApplicationContext.run(EmbeddedServer::class.java, config, "cachepersist").use {
            val counter = it.applicationContext.getBean(CounterService::class.java)
            val count = counter.currentCount("Tim")
            assertEquals(666, count)
        }
    }
}
