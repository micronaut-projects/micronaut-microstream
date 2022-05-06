package io.micronaut.microstream.docs;

import io.micronaut.cache.annotation.CacheConfig;
import io.micronaut.cache.annotation.CacheInvalidate;
import io.micronaut.cache.annotation.CachePut;
import io.micronaut.cache.annotation.Cacheable;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Singleton
@CacheConfig("counter")
@Requires(property = "spec.name", value = "CacheTest")
public class CounterService {

    Map<String, Long> counters = new HashMap<>();

    @Cacheable
    public Long currentCount(String name) {
        return counters.get(name);
    }

    @CacheInvalidate(parameters = {"name"})
    public void setCount(String name, Long count) {
        counters.put(name, count);
    }
}
