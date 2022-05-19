package io.micronaut.microstream.docs

import io.micronaut.cache.annotation.CacheConfig
import io.micronaut.cache.annotation.CachePut
import io.micronaut.cache.annotation.Cacheable
import jakarta.inject.Singleton

@Singleton
@CacheConfig("counter") // <1>
class CounterService {

    Map<String, Long> counters = [:]

    @Cacheable // <2>
    Long currentCount(String name) {
        return counters.get(name)
    }

    @CachePut(parameters = ["name"]) // <3>
    Long setCount(String name, Long count) {
        counters.put(name, count)
        count
    }
}
