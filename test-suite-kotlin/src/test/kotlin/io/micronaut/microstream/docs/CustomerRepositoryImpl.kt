package io.micronaut.microstream.docs

import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.NonNull
import jakarta.inject.Singleton
import one.microstream.concurrency.XThreads
import one.microstream.storage.embedded.types.EmbeddedStorageManager
import java.util.*
import javax.validation.constraints.NotBlank

@Requires(property = "customer.repository", value = "embedded-storage-manager")
//tag::clazz[]
@Singleton
class CustomerRepositoryImpl(private val embeddedStorageManager: EmbeddedStorageManager) // <1>
    : CustomerRepository {
    override fun save(customerSave: CustomerSave): Customer {
        val id = UUID.randomUUID().toString()
        val customer = Customer(id, customerSave.firstName, customerSave.lastName)
        XThreads.executeSynchronized {
            data.customers[customer.id] = customer
            embeddedStorageManager.store(data.customers) // <3>
        }
        return customer
    }

    override fun update(customer: Customer) {
        XThreads.executeSynchronized { // <2>
            val c: Customer? = data.customers[customer.id]
            if (c != null) {
                c.firstName = customer.firstName
                c.lastName = customer.lastName
                embeddedStorageManager.store(c) // <3>
            }
        }
    }

    @NonNull
    override fun findById(id: @NotBlank String): Customer? {
        return data.customers[id]
    }

    override fun deleteById(id: @NotBlank String) {
        XThreads.executeSynchronized { // <2>
            data.customers.remove(id)
            embeddedStorageManager.store(data.customers) // <3>
        }
    }

    private val data: Data
        get() {
            val root = embeddedStorageManager.root()
            return if (root is Data) root else throw RuntimeException("Root is not Data")
        }
}
//end::clazz[]
