package io.micronaut.microstream.docs;

import io.micronaut.cache.annotation.CacheConfig;
import io.micronaut.cache.annotation.CachePut;
import io.micronaut.cache.annotation.Cacheable;
import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

@Singleton
@CacheConfig("counter") // <1>
public class CounterService {

    Map<String, Long> counters = new HashMap<>();

    @Cacheable // <2>
    public Long currentCount(String name) {
        return counters.get(name);
    }

    @CachePut(parameters = {"name"}) // <3>
    public Long setCount(String name, Long count) {
        counters.put(name, count);
        return count;
    }
}
