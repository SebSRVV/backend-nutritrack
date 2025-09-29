// modules/auth/domain/Sex.java
package com.sebsrvv.app.modules.auth.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Sex {
    MALE, FEMALE;

    @JsonCreator
    public static Sex from(String value) {
        return value == null ? null : Sex.valueOf(value.trim().toUpperCase());
    }
}
