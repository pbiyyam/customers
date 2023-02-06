package com.domain.customer.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NoSuchCustomerExistsException extends RuntimeException {
    private String message;
    public NoSuchCustomerExistsException(String msg) {
        super(msg);
        this.message = msg;
    }
}
