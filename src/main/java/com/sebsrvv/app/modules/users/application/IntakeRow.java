// src/main/java/com/sebsrvv/app/modules/users/application/IntakeRow.java
package com.sebsrvv.app.modules.users.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public class IntakeRow {
    @JsonProperty("log_date")
    public LocalDate logDate;

    @JsonProperty("calories")
    public Long calories;       // BIGINT en SQL

    @JsonProperty("goal_kcal")
    public Integer goalKcal;
}
