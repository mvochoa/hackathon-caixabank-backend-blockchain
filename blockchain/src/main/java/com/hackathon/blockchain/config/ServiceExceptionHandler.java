package com.hackathon.blockchain.config;

import com.hackathon.blockchain.dto.ResponseMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class ServiceExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = {ResponseStatusException.class})
    public ResponseEntity<Object> handleResponseStatusException(
            ResponseStatusException ex, WebRequest request) {
        log.error("[Error] ResponseStatusException Exception occurred: {}", ex.getMessage());
        return handleExceptionInternal(
                ex,
                ResponseMessageDto.builder()
                        .message(ex.getReason())
                        .build(),
                new HttpHeaders(),
                ex.getStatusCode(),
                request);
    }
}
