package io.micronaut.microstream.docs

import io.micronaut.core.annotation.Introspected

@Introspected
class Data {
    Map<String, Customer> customers = [:]
}
