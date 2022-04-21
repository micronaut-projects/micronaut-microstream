package io.micronaut.microstream.docs;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;

import javax.validation.constraints.NotBlank;

@Introspected
public class Customer {
    @NonNull
    @NotBlank
	private final String id;

    @NonNull
    @NotBlank
	private final String firstName;

    @Nullable
	private final String lastName;

    public Customer(@NonNull String id, @NonNull String firstName, @Nullable String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @NonNull
    public String getId() {
        return id;
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
