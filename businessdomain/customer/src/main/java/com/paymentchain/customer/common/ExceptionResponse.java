/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.paymentchain.customer.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Duoc
 */
@Schema(description = "Este modelo permite manejar las excepciones [RFC7230]")
@NoArgsConstructor
@Data
public class ExceptionResponse {
    
    @Schema(description = "Es el tipo de excepción",name = "type"
            ,requiredMode = Schema.RequiredMode.REQUIRED
            , example = "/error/authentication/not-autrized")
    private String type;
    
    @Schema(description = "Es el tipo de excepción",name = "title"
            ,requiredMode = Schema.RequiredMode.REQUIRED
            , example = "/error/authentication/not-autrized")
    private String title;
    
    @Schema(description = "Es el tipo de excepción",name = "code"
            ,requiredMode = Schema.RequiredMode.NOT_REQUIRED
            , example = "/error/authentication/not-autrized")
    private String code;
    
    @Schema(description = "Es el tipo de excepción",name = "detail"
            ,requiredMode = Schema.RequiredMode.REQUIRED
            , example = "/error/authentication/not-autrized")
    private String detail;
    
    @Schema(description = "Es el tipo de excepción",name = "instance"
            ,requiredMode = Schema.RequiredMode.REQUIRED
            , example = "/error/authentication/not-autrized")
    private String instance;

    public ExceptionResponse(String type, String title, String code, String detail, String instance) {
        this.type = type;
        this.title = title;
        this.code = code;
        this.detail = detail;
        this.instance = instance;
    }
    
}
