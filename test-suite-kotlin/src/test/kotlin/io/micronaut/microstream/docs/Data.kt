package io.micronaut.microstream.docs

import io.micronaut.core.annotation.Introspected
import io.micronaut.core.annotation.NonNull

@Introspected // <1>
class Data(private val customers: MutableMap<String, Customer> = mutableMapOf()) {

    @NonNull
    fun findById(@NonNull id: String): Customer? {
        return customers[id]
    }

    @NonNull
    fun getCustomers(): Collection<Customer> {
        return customers.values
    }

    fun add(@NonNull customer: Customer) {
        customers[customer.id] = customer
    }

    fun remove(@NonNull id: String) {
        customers.remove(id)
    }
}