package io.micronaut.microstream.rest

import io.micronaut.core.annotation.Introspected
import io.micronaut.core.annotation.NonNull

@Introspected
class People {

    @NonNull
    List<String> people = []
}
