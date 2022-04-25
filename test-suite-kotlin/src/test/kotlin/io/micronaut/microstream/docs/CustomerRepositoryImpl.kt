package io.micronaut.microstream.docs

import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.NonNull
import jakarta.inject.Singleton
import one.microstream.concurrency.XThreads
import one.microstream.storage.embedded.types.EmbeddedStorageManager
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Requires(property = "customer.repository", value = "embedded-storage-manager")
//tag::clazz[]
@Singleton
class CustomerRepositoryImpl(private val embeddedStorageManager: EmbeddedStorageManager) // <1>
    : CustomerRepository {
    override fun save(customer: @Valid Customer) {
        XThreads.executeSynchronized { // <2>
            if (data != null) {
                data!!.customers[customer.id] = customer
                embeddedStorageManager.store(data!!.customers) // <3>
            }

        }
    }

    @NonNull
    override fun findById(id: @NotBlank String): Customer? {
        return if (data != null) data!!.customers[id] else null
    }

    override fun deleteById(id: @NotBlank String) {
        XThreads.executeSynchronized { // <2>
            if (data != null) {
                data!!.customers.remove(id)
                embeddedStorageManager.store(data!!.customers) // <3>
            }
        }
    }

    private val data: Data?
        get() {
            val root = embeddedStorageManager.root()
            return if (root is Data) root else null
        }
}
//end::clazz[]
