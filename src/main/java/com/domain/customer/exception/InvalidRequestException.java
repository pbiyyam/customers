package com.domain.customer.exception;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class InvalidRequestException extends RuntimeException {

    private String message;

    public InvalidRequestException(String msg){
        super(msg);
        this.message = msg;
    }
}
