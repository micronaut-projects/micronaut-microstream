package io.micronaut.microstream.docs

import io.micronaut.core.annotation.Introspected

@Introspected // <1>
data class Data(val customers: MutableMap<String, Customer> = mutableMapOf())
