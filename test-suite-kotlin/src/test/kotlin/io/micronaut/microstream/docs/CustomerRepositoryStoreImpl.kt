package io.micronaut.microstream.docs

import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.NonNull
import io.micronaut.core.annotation.Nullable
import io.micronaut.microstream.RootProvider
import io.micronaut.microstream.annotations.Store
import io.micronaut.microstream.annotations.StoreParams
import io.micronaut.microstream.annotations.StoreReturn
import jakarta.inject.Singleton
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Requires(property = "customer.repository", value = "store")
//tag::clazz[]
@Singleton
open class CustomerRepositoryStoreImpl(private val rootProvider: RootProvider<Data>) // <1>
    : CustomerRepository {
    override fun save(customerSave: @Valid CustomerSave): Customer {
        return addCustomer(rootProvider.root().customers, customerSave)
    }

    override fun update(id : @NotBlank String,
                        customerSave: @Valid CustomerSave) {
        updateCustomer(id, customerSave)
    }

    @NonNull
    override fun findById(id: @NotBlank String): Customer? {
        return rootProvider.root().customers[id]
    }

    override fun deleteById(id: @NotBlank String) {
        removeCustomer(rootProvider.root().customers, id)
    }

    @StoreReturn // <2>
    @Nullable
    open fun updateCustomer(id: String, customerSave: CustomerSave): Customer? {
        val c: Customer? = rootProvider.root().customers[id]
        return if (c != null) {
            c.firstName = customerSave.firstName
            c.lastName = customerSave.lastName
            c
        } else null
    }

    @StoreParams("customers") // <3>
    open fun addCustomer(customers: MutableMap<String, Customer>, customerSave: CustomerSave): Customer {
        val customer = Customer(
            UUID.randomUUID().toString(),
            customerSave.firstName,
            customerSave.lastName
        )
        customers[customer.id] = customer
        return customer
    }

    @StoreParams("customers") // <3>
    open fun removeCustomer(customers: MutableMap<String, Customer>, id: String) {
        customers.remove(id)
    }
}
//end::clazz[]
