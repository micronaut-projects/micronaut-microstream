package io.micronaut.microstream.docs

import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.NonNull
import io.micronaut.core.annotation.Nullable
import io.micronaut.microstream.annotation.Store
import jakarta.inject.Singleton
import one.microstream.storage.types.StorageManager
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Requires(property = "customer.repository", value = "store")
//tag::clazz[]
@Singleton
open class CustomerRepositoryStoreImpl(private val storageManager: StorageManager) // <1>
    : CustomerRepository {
    override fun save(customerSave: @Valid CustomerSave): Customer {
        return addCustomer(data.customers, customerSave)
    }

    override fun update(id : @NotBlank String,
                        customerSave: @Valid CustomerSave) {
        updateCustomer(id, customerSave)
    }

    @NonNull
    override fun findById(id: @NotBlank String): Customer? {
        return data.customers[id]
    }

    override fun deleteById(id: @NotBlank String) {
        removeCustomer(data.customers, id)
    }

    @Store(result = true) // <2>
    @Nullable
    open fun updateCustomer(id: String, customerSave: CustomerSave): Customer? {
        val c: Customer? = data.customers[id]
        return if (c != null) {
            c.firstName = customerSave.firstName
            c.lastName = customerSave.lastName
            c
        } else null
    }

    @Store(parameters = ["customers"]) // <3>
    open fun addCustomer(customers: MutableMap<String, Customer>, customerSave: CustomerSave): Customer {
        val customer = Customer(
            UUID.randomUUID().toString(),
            customerSave.firstName,
            customerSave.lastName
        )
        customers[customer.id] = customer
        return customer
    }

    @Store(parameters = ["customers"]) // <3>
    open fun removeCustomer(customers: MutableMap<String, Customer>, id: String) {
        customers.remove(id)
    }

    private val data: Data
        get() {
            val root = storageManager.root()
            return if (root is Data) root else throw RuntimeException("Root is not Data")
        }
}
//end::clazz[]
