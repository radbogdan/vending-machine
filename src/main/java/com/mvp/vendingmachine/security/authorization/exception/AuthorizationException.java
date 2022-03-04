package com.mvp.vendingmachine.security.authorization.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception defined in order to override the {@link HttpStatus} code.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthorizationException extends AuthorizationServiceException {

    public AuthorizationException(String msg) {
        super(msg);
    }

    public AuthorizationException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
