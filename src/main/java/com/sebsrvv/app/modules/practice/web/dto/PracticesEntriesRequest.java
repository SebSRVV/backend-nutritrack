package com.sebsrvv.app.modules.practice.web.dto;


import jakarta.persistence.Column;
import lombok.Data;

import java.util.Date;

@Data
public class PracticesEntriesRequest {
    private Date logDate;
    private Number value;
    private String note;
    private Boolean achieved;
}
