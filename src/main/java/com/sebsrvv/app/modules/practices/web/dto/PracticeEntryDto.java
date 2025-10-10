// src/main/java/com/sebsrvv/app/practices/dto/PracticeEntryDto.java
package com.sebsrvv.app.modules.practices.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PracticeEntryDto(
        String id,           // Identificador de la practica
        String practice_id,  // Identificador de la practica
        String log_date,     // Fecha del ingreso en formato anio / mes / dia
        Double value,        // Comprobar si se encuentra activa
        String note, // Nota del usuario de la practica
        Boolean achieved //Comprobar si se cumplio la meta
) {}
