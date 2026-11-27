package com.taiyidu.gateway.exception;

import lombok.Getter;

@Getter
public class UnauthorizedException extends RuntimeException {
    private final int code;

    public UnauthorizedException(String message) {
        super(message);
        this.code = 401;
    }
}