package io.micronaut.microstream.docs

import io.micronaut.core.annotation.Introspected

@Introspected // <1>
class Data {
    Map<String, Customer> customers = [:]
}
