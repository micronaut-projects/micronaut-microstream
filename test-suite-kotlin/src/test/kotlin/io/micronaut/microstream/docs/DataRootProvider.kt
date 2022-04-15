package io.micronaut.microstream.docs

import io.micronaut.microstream.conf.RootInstanceProvider
import jakarta.inject.Named
import jakarta.inject.Singleton

@Named("main")
@Singleton
class DataRootProvider : RootInstanceProvider<Data> {
    override fun rootInstance(): Data = Data()
}
