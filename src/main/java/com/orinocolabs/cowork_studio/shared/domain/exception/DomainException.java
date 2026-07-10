package com.orinocolabs.cowork_studio.shared.domain.exception;

/**
 * Base type for every business rule violation raised by the domain layer.
 *
 * <p>Kept deliberately free of any framework dependency (no Spring, no JPA) so
 * that the domain model can be compiled and tested in complete isolation.
 * Infrastructure adapters (e.g. the web layer) are responsible for mapping
 * subtypes of this exception to the appropriate transport-level response
 * (HTTP status code, error payload, etc.).
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }
}
