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
	private String firstName;

    @Nullable
	private String lastName;

    public Customer(@NonNull String id, @NonNull String firstName, @Nullable String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setFirstName(@NonNull String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(@Nullable String lastName) {
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
