package io.micronaut.microstream.docs;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import jakarta.validation.constraints.NotBlank;

@Serdeable
public class CustomerSave {

    @NonNull
    @NotBlank
    private String firstName;

    @Nullable
    private String lastName;

    public CustomerSave(@NonNull String firstName, @Nullable String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @NonNull
    public String getFirstName() {
        return firstName;
    }

    @Nullable
    public String getLastName() {
        return lastName;
    }
}



