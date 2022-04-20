package io.micronaut.microstream.docs

import io.micronaut.core.annotation.NonNull
import io.micronaut.microstream.annotation.StoreAll
import jakarta.inject.Singleton
import one.microstream.storage.embedded.types.EmbeddedStorageManager
import java.util.stream.Collectors
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Singleton
open class CustomerRepositoryImpl(private val embeddedStorageManager: EmbeddedStorageManager) // <1>
    : CustomerRepository {
    @StoreAll
    override fun save(customer: @Valid Customer) {
        if (data != null) {
            data!!.add(customer)
        }
    }

    @NonNull
    override fun findById(id: @NotBlank String): Customer? {
        return if (data != null) data!!.findById(id) else null
    }

    @StoreAll
    override fun deleteById(id: @NotBlank String) {
        if (data != null) {
            data!!.remove(id)
        }
    }

    @NonNull
    override fun findByFirstName(firstName: @NotBlank String): Collection<Customer> {
        val customers = if (data != null) data!!.getCustomers() else emptyList()
        return customersByFirstName(customers, firstName)
    }

    private val data: Data?
        get() {
            val root = embeddedStorageManager.root()
            return if (root is Data) root else null
        }

    companion object {
        private fun customersByFirstName(
            @NonNull customers: Collection<Customer>,
            @NonNull firstName: String?
        ): List<Customer> {
            return customers.stream()
                .filter { c: Customer -> c.firstName == firstName }
                .collect(Collectors.toList())
        }
    }
}
