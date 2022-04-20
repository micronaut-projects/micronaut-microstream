package io.micronaut.microstream.docs

import io.micronaut.microstream.conf.RootInstanceProvider
import jakarta.inject.Named
import jakarta.inject.Singleton

@Named("one-microstream-instance") // <1>
@Singleton
class DataRootProvider implements RootInstanceProvider<Data> {

    @Override
    Data rootInstance() {
        new Data()
    }
}
