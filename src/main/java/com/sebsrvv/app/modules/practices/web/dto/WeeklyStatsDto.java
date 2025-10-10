// src/main/java/com/sebsrvv/app/practices/dto/WeeklyStatsDto.java
package com.sebsrvv.app.modules.practices.web.dto;

public record WeeklyStatsDto(
        String practice_id, //Identificador de la practica
        String name, //Nombre de la practica
        Integer days_per_week, //Cantidad de dias por semana
        Integer achieved_days_last7, // Comprobar si se pudo dentro de los 7 dias
        Integer logged_days_last7 // Comprobar si esta dentro de los 7 dias
) {}
