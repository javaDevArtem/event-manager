package com.amir.eventmanager.location.web;

import com.amir.eventmanager.location.LocationController;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final static Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleCommonException(Exception e) {
        log.error("Handle common exception", e);
        ErrorMessageResponse messageResponse = new ErrorMessageResponse("Internal error", e.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(500)
                .body(messageResponse);
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(Exception e) {
        log.error("Handle entity not found exception", e);
        ErrorMessageResponse messageResponse = new ErrorMessageResponse("Entity not found", e.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(404)
                .body(messageResponse);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e) {
        log.error("Handle bad request exception", e);
        ErrorMessageResponse messageResponse = new ErrorMessageResponse("Bad request", e.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(400)
                .body(messageResponse);
    }
}
