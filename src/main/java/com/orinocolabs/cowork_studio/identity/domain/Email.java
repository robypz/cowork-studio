package com.orinocolabs.cowork_studio.identity.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object enforcing the "an email must look like an email" invariant at
 * construction time, so no other layer needs to re-validate it once a
 * {@code User} exists in memory.
 */
public final class Email {

    private static final Pattern FORMAT =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    private final String value;

    private Email(String value) {
        this.value = value;
    }

    public static Email of(String rawValue) {
        String normalized = Objects.requireNonNullElse(rawValue, "").trim().toLowerCase();
        if (!FORMAT.matcher(normalized).matches()) {
            throw new InvalidEmailException(rawValue);
        }
        return new Email(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email other)) return false;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
