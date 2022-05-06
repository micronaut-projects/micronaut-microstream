package io.micronaut.microstream.docs

import io.micronaut.cache.annotation.CacheConfig
import io.micronaut.cache.annotation.CachePut
import io.micronaut.cache.annotation.Cacheable
import jakarta.inject.Singleton
import java.util.HashMap

@Singleton
@CacheConfig("counter") // <1>
open class CounterService {

    val counters: MutableMap<String, Long> = HashMap()

    @Cacheable // <2>
    open fun currentCount(name: String): Long? = counters[name]

    @CachePut(parameters = ["name"]) // <3>
    open fun setCount(name: String, count: Long): Long {
        counters[name] = count
        return count
    }
}
