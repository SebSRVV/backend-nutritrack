// src/main/java/com/sebsrvv/app/practices/dto/PracticeDto.java
package com.sebsrvv.app.modules.practices.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PracticeDto(
        String id,          // Identificador del usuario
        String name,  //Nombre de la practica
        String description,  //Descripcion de la practica
        String icon, //Icono de la practica
        String value_kind,  // Identificador en caso sea de cantidad o por llano
        Double target_value, // Valor de la practica
        String target_unit, //Meta de la practica
        String operator,    // Operador
        Integer days_per_week, //Dias por semana para la meta de la practica
        Boolean is_active //Confirmar si se encuetra activa la practica
) {}
