package com.islamhamada.petshop.exception;

import com.islamhamada.petshop.contracts.model.RestExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ResResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ProductServiceException.class)
    public ResponseEntity<RestExceptionResponse> handleProductServiceException(ProductServiceException exception){
        return new ResponseEntity<>(RestExceptionResponse.builder()
                .error_code(exception.getError_code())
                .error_message(exception.getMessage())
                .build(), exception.httpStatus);
    }
}
