package com.islamhamada.petshop.exception;

import com.islamhamada.petshop.contracts.exception.ServiceException;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ProductServiceException extends ServiceException {
    HttpStatus httpStatus;

    public ProductServiceException(String message, String error_code, HttpStatus httpStatus){
        super(message, "PRODUCT_" + error_code);
        this.httpStatus = httpStatus;
    }
}
