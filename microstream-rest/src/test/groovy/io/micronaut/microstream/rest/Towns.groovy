package io.micronaut.microstream.rest

import io.micronaut.core.annotation.NonNull
import io.micronaut.serde.annotation.Serdeable

@Serdeable
class Towns {

    @NonNull
    List<String> towns = []
}
