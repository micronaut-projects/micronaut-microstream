package io.micronaut.microstream.s3;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class Root {

    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
