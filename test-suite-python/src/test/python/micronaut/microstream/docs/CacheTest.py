import uuid
from typing import Annotated

from jakarta.inject import Inject
from micronaut.context.annotation import Property
from micronaut.test.extensions.junit5.annotation import MicronautTest
from org.junit.jupiter.api import Test

from .CounterService import CounterService


@MicronautTest(environments=["cache"])
@Property(name="storageDirectory", value="build/microstream-cache-" + str(uuid.uuid4()))
class CacheTest:

    counter: Annotated[CounterService, Inject]

    @Test
    def cache_works_as_expected(self) -> None:
        # When we use a cached method
        self.counter.set_count("Tim", 1337)
        count = self.counter.current_count("Tim")
        assert count == 1337

        # Change the store so we check we're seeing a cached value
        self.counter.counters["Tim"] = -1
        count = self.counter.current_count("Tim")
        assert count == 1337
