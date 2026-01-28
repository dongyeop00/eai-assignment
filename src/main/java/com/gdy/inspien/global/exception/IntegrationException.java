package com.gdy.inspien.global.exception;

import lombok.Getter;

@Getter
public class IntegrationException extends RuntimeException {

    private final ErrorCode errorCode;

    public IntegrationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public IntegrationException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
