package com.sebsrvv.app.modules.practice.web.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class DeleteResponse {
    private UUID id;
    private boolean isActive;
}
