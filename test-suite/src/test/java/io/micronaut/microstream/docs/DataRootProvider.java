package io.micronaut.microstream.docs;

import io.micronaut.microstream.conf.RootInstanceProvider;
import jakarta.inject.Named;

@Named("main")
public class DataRootProvider implements RootInstanceProvider<Data> {
    @Override
    public Data rootInstance() {
        return new Data();
    }
}
