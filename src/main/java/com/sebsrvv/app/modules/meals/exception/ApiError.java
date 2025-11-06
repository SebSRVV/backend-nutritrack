package com.sebsrvv.app.modules.meals.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
//Representa la estructura del error devuelto al cliente
@Data
@AllArgsConstructor
public class ApiError {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
