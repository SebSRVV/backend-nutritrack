package com.sebsrvv.app.modules.practice.web.dto;

import lombok.Data;

import java.util.Date;

@Data
public class PracticesEntriesResponse {
    private Date logDate;
    private Number value;
    private String note;
    private Boolean achieved;
}
