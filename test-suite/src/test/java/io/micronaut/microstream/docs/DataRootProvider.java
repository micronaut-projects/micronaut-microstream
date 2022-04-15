package io.micronaut.microstream.docs;

import io.micronaut.microstream.conf.RootInstanceProvider;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Named("main")
@Singleton
public class DataRootProvider implements RootInstanceProvider<Data> {
    @Override
    public Data rootInstance() {
        return new Data();
    }
}
