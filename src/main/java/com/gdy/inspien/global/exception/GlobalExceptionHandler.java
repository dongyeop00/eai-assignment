package com.gdy.inspien.global.exception;

import com.gdy.inspien.global.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IntegrationException.class)
    public ApiResponse<Void> handleIntegrationException(IntegrationException e) {

        log.error("연계 오류 발생: [{}] {}", e.getErrorCode().getCode(), e.getMessage(), e);

        ErrorCode errorCode = e.getErrorCode();

        return ApiResponse.fail(errorCode.getCode(), errorCode.getMessage());

    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception e) {

        log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);

        return ApiResponse.fail(ErrorCode.SYSTEM_ERROR.getCode(), ErrorCode.SYSTEM_ERROR.getMessage());

    }
}