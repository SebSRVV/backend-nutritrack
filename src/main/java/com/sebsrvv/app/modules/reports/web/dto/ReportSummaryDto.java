package com.sebsrvv.app.modules.reports.web.dto;

import java.util.Map;

public record ReportSummaryDto(
        Map<String, Object> summary
) {}
