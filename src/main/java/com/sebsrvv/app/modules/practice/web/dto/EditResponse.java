package com.sebsrvv.app.modules.practice.web.dto;
import lombok.Data;

import java.util.UUID;

@Data
public class EditResponse {
    private UUID id;
    private String practiceName;
    private String description;
    private String icon;
    private Integer frequencyTarget;
    private Boolean isActive;
    private String updatedAt;
}
