// src/main/java/com/sebsrvv/app/modules/goals/web/dto/GoalRequest.java
package com.sebsrvv.app.modules.goals.web.dto;

import com.sebsrvv.app.modules.goals.web.validation.ValidDateRange;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ValidDateRange(message = "El rango de fechas es inválido: La fecha de inicio no puede ser posterior a la fecha de fin.")
public class GoalRequest {

    // Validaciones para el usuario final (errores 400)
    @NotBlank(message = "El nombre de la meta es obligatorio y no puede ir vacío.")
    private String goal_name;
    private String description;

    @Min(value = 1, message = "La frecuencia semanal debe ser de 1 día como mínimo.")
    @Max(value = 7, message = "La frecuencia semanal debe ser de 7 días como máximo.")
    private Integer weekly_target;

    @NotNull(message = "Debes especificar si la meta está activa (true/false).")
    private Boolean is_active;

    private Integer category_id;
    private String value_type;
    private String unit;

    // Las fechas se validan a nivel de clase con @ValidDateRange
    private LocalDate start_date;
    private LocalDate end_date;

    private BigDecimal target_value;
}