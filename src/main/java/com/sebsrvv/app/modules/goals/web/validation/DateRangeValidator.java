// src/main/java/com/sebsrvv/app/modules/goals/web/validation/DateRangeValidator.java
package com.sebsrvv.app.modules.goals.web.validation;

import com.sebsrvv.app.modules.goals.web.dto.GoalRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

// ¡Esta es la implementación completa y correcta!
public class DateRangeValidator implements ConstraintValidator<ValidDateRange, GoalRequest> {

    private String message; // Variable para guardar el mensaje de error

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
        // 1. Guarda el mensaje de la anotación cuando se inicializa
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(GoalRequest request, ConstraintValidatorContext context) {
        if (request == null) return false;

        LocalDate s = request.getStart_date();
        LocalDate e = request.getEnd_date();

        // Si ambas fechas son nulas, la validación se ignora
        if (s == null || e == null) return true;

        // Si la fecha de fin es ANTES de la fecha de inicio, falla.
        if (e.isBefore(s)) {
            // 2. Construye la violación de error
            context.disableDefaultConstraintViolation(); // Desactiva el error a nivel de clase
            context.buildConstraintViolationWithTemplate(this.message) // Usa el mensaje guardado
                    .addPropertyNode("end_date") // Asigna el error al campo 'end_date'
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}