package io.micronaut.microstream.docs

import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.server.EmbeddedServer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID

class CacheTest {

    @Test
    fun cacheWorksAsExpected() {
        val config = mapOf("storageDirectory" to "build/microstream-cache-${UUID.randomUUID()}")
        ApplicationContext.run(EmbeddedServer::class.java, config, "cache").use {
            val counter = it.applicationContext.getBean(CounterService::class.java)
            counter.setCount("Tim", 1337)

            var currentCount = counter.currentCount("Tim")
            assertEquals(1337, currentCount)

            // Change the value in the map to check we are using the cache
            counter.counters["Tim"] = 42

            currentCount = counter.currentCount("Tim")
            assertEquals(1337, currentCount)
        }
    }
}
