package com.islamhamada.petshop.exception;

import com.islamhamada.petshop.contracts.exception.ServiceExceptionHandler;
import com.islamhamada.petshop.contracts.model.RestExceptionResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Log4j2
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ServiceExceptionHandler {

    @ExceptionHandler(ProductServiceException.class)
    public ResponseEntity<RestExceptionResponse> handleProductServiceException(ProductServiceException exception){
        log.error(exception);
        return new ResponseEntity<>(RestExceptionResponse.builder()
                .error_code(exception.getError_code())
                .error_message(exception.getMessage())
                .build(), exception.getHttpStatus());
    }
}
