package com.orinocolabs.cowork_studio.identity.domain;

/**
 * Wraps an already-hashed password. The domain never sees, stores, or
 * compares raw passwords directly — it only accepts values produced by a
 * {@link PasswordHasher} adapter, so the aggregate cannot accidentally end up
 * persisting plaintext.
 */
public final class HashedPassword {

    private final String hash;

    private HashedPassword(String hash) {
        this.hash = hash;
    }

    /**
     * Wraps a value that has already gone through {@link PasswordHasher#hash}.
     * Not meant to be called with a raw/plaintext password.
     */
    public static HashedPassword ofHash(String hash) {
        if (hash == null || hash.isBlank()) {
            throw new IllegalArgumentException("hashed password must not be blank");
        }
        return new HashedPassword(hash);
    }

    public String value() {
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HashedPassword other)) return false;
        return hash.equals(other.hash);
    }

    @Override
    public int hashCode() {
        return hash.hashCode();
    }

    @Override
    public String toString() {
        return "HashedPassword[protected]";
    }
}
