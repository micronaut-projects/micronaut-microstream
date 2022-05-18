package io.micronaut.microstream

import io.micronaut.microstream.health.MicroStreamHealth
import one.microstream.storage.types.StorageManager
import spock.lang.Specification

class DefaultRootProviderSpec extends Specification {

    void "DefaultRootProvider casts StoreManager::root"() {
        def storageManager = Stub(StorageManager) {
            root() >> new MicroStreamHealth(false, true, true, false, false, false)
        }
        expect:
        new DefaultRootProvider<>(storageManager).root() instanceof MicroStreamHealth
    }
}
