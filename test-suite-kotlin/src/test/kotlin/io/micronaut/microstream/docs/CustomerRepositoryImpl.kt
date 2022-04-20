package io.micronaut.microstream.docs

import io.micronaut.core.annotation.NonNull
import jakarta.inject.Singleton
import one.microstream.concurrency.XThreads
import one.microstream.storage.embedded.types.EmbeddedStorageManager
import java.util.*
import java.util.stream.Collectors
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Singleton
class CustomerRepositoryImpl(private val embeddedStorageManager: EmbeddedStorageManager) // <1>
    : CustomerRepository {
    override fun save(customer: @Valid Customer) {
        XThreads.executeSynchronized {
            data.ifPresent { data: Data ->
                data.add(customer)
            }
            embeddedStorageManager.storeAll()
        }
    }

    @NonNull
    override fun findById(id: @NotBlank String): Optional<Customer> {
        return data.flatMap { data: Data ->
            data.findById(id)
        }
    }

    override fun deleteById(id: @NotBlank String) {
        XThreads.executeSynchronized {
            data.ifPresent { data: Data ->
                data.remove(id)
            }
            embeddedStorageManager.storeAll()
        }
    }

    @NonNull
    override fun findByFirstName(firstName: @NotBlank String): Collection<Customer> {
        return customersByFirstName(
            data
                .map { obj: Data -> obj.getCustomers() }
                .orElseGet { emptyList() }, firstName
        )
    }

    private val data: Optional<Data>
        get() {
            val root = embeddedStorageManager.root()
            return if (root is Data) {
                Optional.of(root)
            } else Optional.empty()
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
