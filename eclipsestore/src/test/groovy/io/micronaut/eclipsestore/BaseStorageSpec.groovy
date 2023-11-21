package io.micronaut.eclipsestore

import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.Introspected
import jakarta.inject.Singleton
import org.eclipse.serializer.concurrency.XThreads
import org.eclipse.store.storage.types.StorageManager
import spock.lang.Specification

abstract class BaseStorageSpec extends Specification {

    @Singleton
    @Requires(property = "spec.type", value = "storage")
    static class CustomerRepository {

        private final StorageManager storageManager;

        CustomerRepository(StorageManager storageManager) {
            this.storageManager = storageManager
        }

        String name() {
            data().name
        }

        void updateName(String name) {
            XThreads.executeSynchronized(() -> {
                data().name = name
                storageManager.storeRoot();
            });
        }

        private Root data() {
            return (Root) storageManager.root();
        }
    }

    @Introspected
    static class Root {

        String name;

        String getName() {
            return name;
        }

        void setName(String name) {
            this.name = name;
        }
    }
}
