package com.orinocolabs.cowork_studio.shared.infrastructure.web;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.orinocolabs.cowork_studio.identity.domain.exception.EmailAlreadyRegisteredException;
import com.orinocolabs.cowork_studio.identity.domain.exception.InvalidCredentialsException;
import com.orinocolabs.cowork_studio.identity.domain.exception.UserAlreadyDeactivatedException;
import com.orinocolabs.cowork_studio.identity.domain.exception.UserDeactivatedException;
import com.orinocolabs.cowork_studio.identity.domain.exception.UserNotFoundException;
import com.orinocolabs.cowork_studio.shared.domain.exception.DomainException;

/**
 * Central place mapping domain rule violations (and request validation
 * errors) to HTTP responses, so no controller needs its own try/catch.
 * New bounded contexts only need to add their "already exists"/"invalid
 * state" exceptions to the CONFLICT list below — everything else falls
 * back to 400.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ErrorResponse handleInvalidCredentials(DomainException ex) {
        return ErrorResponse.builder(ex, HttpStatus.UNAUTHORIZED, ex.getMessage()).build();
    }

    @ExceptionHandler(UserDeactivatedException.class)
    public ErrorResponse handleUserDeactivated(DomainException ex) {
        return ErrorResponse.builder(ex, HttpStatus.FORBIDDEN, ex.getMessage()).build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ErrorResponse handleUserNotFound(DomainException ex) {
        return ErrorResponse.builder(ex, HttpStatus.NOT_FOUND, ex.getMessage()).build();
    }

    @ExceptionHandler({
            EmailAlreadyRegisteredException.class,
            UserAlreadyDeactivatedException.class
    })
    public ErrorResponse handleConflict(DomainException ex) {
        return ErrorResponse.builder(ex, HttpStatus.CONFLICT, ex.getMessage()).build();
    }

    @ExceptionHandler(DomainException.class)
    public ErrorResponse handleDomainException(DomainException ex) {
        return ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage()).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, detail).build();
    }
}
