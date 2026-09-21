package io.micronaut.microstream.docs

import io.micronaut.serde.annotation.Serdeable

@Serdeable // <1>
class Customer(val id: String, var firstName: String, var lastName: String?)
