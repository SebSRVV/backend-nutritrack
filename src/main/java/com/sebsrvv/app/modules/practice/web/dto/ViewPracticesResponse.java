package com.sebsrvv.app.modules.practice.web.dto;
import lombok.Data;

import java.util.UUID;

@Data
public class ViewPracticesResponse {
    private UUID id;
    private String PracticeName;
    private String Description;
    private String Icon;
    private int frequencyTarget;
    private boolean isActive;
}