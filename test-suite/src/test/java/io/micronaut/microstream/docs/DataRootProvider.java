package io.micronaut.microstream.docs;

import io.micronaut.microstream.conf.RootInstanceProvider;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Named("main") // <1>
@Singleton
public class DataRootProvider implements RootInstanceProvider<Data> {
    @Override
    public Data rootInstance() {
        return new Data();
    }
}
