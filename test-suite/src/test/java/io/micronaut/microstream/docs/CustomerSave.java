package io.micronaut.microstream.docs;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import javax.validation.constraints.NotBlank;

@Introspected
public class CustomerSave {

    @NonNull
    @NotBlank
    private final String firstName;

    @Nullable
    private final String lastName;

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



