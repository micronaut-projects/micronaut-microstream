package io.micronaut.eclipsestore.docs

import io.micronaut.core.annotation.Introspected

@Introspected
class Customer(val id: String, var firstName: String, var lastName: String?)
