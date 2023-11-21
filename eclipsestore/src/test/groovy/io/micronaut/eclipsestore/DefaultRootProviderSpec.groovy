package io.micronaut.eclipsestore

import io.micronaut.eclipsestore.health.EclipseStoreHealth
import org.eclipse.store.storage.types.StorageManager
import spock.lang.Specification

class DefaultRootProviderSpec extends Specification {

    void "DefaultRootProvider casts StoreManager::root"() {
        def storageManager = Stub(StorageManager) {
            root() >> new EclipseStoreHealth(false, true, true, false, false, false)
        }
        expect:
        new DefaultRootProvider<>(storageManager).root() instanceof EclipseStoreHealth
    }
}
