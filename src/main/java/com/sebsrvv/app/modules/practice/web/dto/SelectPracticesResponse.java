package com.sebsrvv.app.modules.practice.web.dto;
import lombok.Data;

import java.util.UUID;

@Data
public class SelectPracticesResponse {
    private UUID id;
    private int DefaultId;
    private String PracticeName;
    private String Description;
    private String Icon;
    private int frequencyTarget;
    private boolean isActive;
    private int sortOrder;
}
