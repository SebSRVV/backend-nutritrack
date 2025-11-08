package com.sebsrvv.app.modules.practice.web.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PracticesEntriesDTO {
    //private Date logDate;
    private BigDecimal value;
    private String note;
    private Boolean achieved;
    private Date loggedAt = new Date();
}
