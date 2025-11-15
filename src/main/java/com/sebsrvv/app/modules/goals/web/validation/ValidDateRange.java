// src/main/java/com/sebsrvv/app/modules/goals/web/validation/ValidDateRange.java
package com.sebsrvv.app.modules.goals.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
@Documented
public @interface ValidDateRange {
    String message() default "El rango de fechas es inválido.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}