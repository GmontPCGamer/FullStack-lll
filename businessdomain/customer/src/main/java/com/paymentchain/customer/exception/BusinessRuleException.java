/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.paymentchain.customer.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 *
 * @author Duoc
 */
@Data
public class BusinessRuleException extends Exception {

    public BusinessRuleException(long id, String code, HttpStatus httpStatus, String message) {
        super(message);
        this.id = id;
        this.code = code;
        this.httpStatus = httpStatus;
    }
    
    public BusinessRuleException(String code, HttpStatus httpStatus, String message) {
        super(message);
        this.id = id;
        this.code = code;
        this.httpStatus = httpStatus;
    }
    
     public BusinessRuleException(HttpStatus httpStatus, String message) {
        super(message);
        this.id = id;
        this.code = code;
        this.httpStatus = httpStatus;
    }


    private long id;
    private String code;
    private HttpStatus httpStatus;

}
