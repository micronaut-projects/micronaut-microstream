from jakarta.inject import Singleton
from java.lang import Long
from micronaut.cache.annotation import CacheConfig, CachePut, Cacheable


@Singleton
@CacheConfig("counter")  # <1>
class CounterService:

    def __init__(self):
        self.counters: dict[str, Long] = {}

    @Cacheable  # <2>
    def current_count(self, name: str) -> Long | None:
        return self.counters.get(name)

    @CachePut(parameters=["name"])  # <3>
    def set_count(self, name: str, count: Long) -> Long:
        self.counters[name] = count
        return count
