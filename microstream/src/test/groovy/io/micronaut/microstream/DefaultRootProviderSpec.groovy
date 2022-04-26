package io.micronaut.microstream

import io.micronaut.microstream.health.MicrostreamHealth
import one.microstream.storage.types.StorageManager
import spock.lang.Specification

class DefaultRootProviderSpec extends Specification {

    void "DefaultRootProvider casts StoreManager::root"() {
        def storageManager = Stub(StorageManager) {
            root() >> new MicrostreamHealth(false, true, true, false, false, false)
        }
        expect:
        new DefaultRootProvider<>(storageManager).root() instanceof MicrostreamHealth
    }
}
