package io.micronaut.microstream.docs

import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.NonNull
import jakarta.inject.Singleton
import one.microstream.concurrency.XThreads
import one.microstream.storage.types.StorageManager
import java.util.*
import javax.validation.constraints.NotBlank

@Requires(property = "customer.repository", value = "embedded-storage-manager")
//tag::clazz[]
@Singleton
class CustomerRepositoryImpl(private val storageManager: StorageManager) // <1>
    : CustomerRepository {
    override fun save(customerSave: CustomerSave): Customer {
        val id = UUID.randomUUID().toString()
        val customer = Customer(id, customerSave.firstName, customerSave.lastName)
        XThreads.executeSynchronized { // <2>
            data.customers[customer.id] = customer
            storageManager.store(data.customers) // <3>
        }
        return customer
    }

    override fun update(id : String, customerSave: CustomerSave) {
        XThreads.executeSynchronized { // <2>
            val customer : Customer? = data.customers[id]
            if (customer != null) {
                with(customer) {
                    firstName = customerSave.firstName
                    lastName = customerSave.lastName
                }
                storageManager.store(customer) // <3>
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
            storageManager.store(data.customers) // <3>
        }
    }

    private val data: Data
        get() {
            val root = storageManager.root()
            return if (root is Data) root else throw RuntimeException("Root is not Data")
        }
}
//end::clazz[]
