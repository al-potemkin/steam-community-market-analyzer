package org.bmarket.steam.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.bmarket.steam.entity.GenericErrorResponse;
import org.bmarket.steam.exception.ItemNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@Log4j2
@RestControllerAdvice
public class ExceptionHandlingAdvice {
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<GenericErrorResponse> handleItemNotFoundException(ItemNotFoundException ex,
                                                                            HttpServletRequest request) {
        log.warn("ItemNotFoundException: [{}]", ex.getMessage());
        return buildException(ex.getMessage(), request, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<GenericErrorResponse> buildException(String message,
                                                                HttpServletRequest request,
                                                                HttpStatus status) {
        var response = GenericErrorResponse.builder()
                .error(status.getReasonPhrase())
                .status(status.value())
                .path(request.getRequestURI())
                .method(request.getMethod())
                .timestamp(OffsetDateTime.now())
                .message(message)
                .build();
        return new ResponseEntity<>(response, status);
    }
}
