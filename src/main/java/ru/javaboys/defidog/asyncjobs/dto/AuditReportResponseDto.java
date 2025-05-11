package ru.javaboys.defidog.asyncjobs.dto;

import lombok.Data;
import ru.javaboys.defidog.entity.AuditScanResutlCriticality;

@Data
public class AuditReportResponseDto {
    private String markdownReport;
    private String description;
    private AuditScanResutlCriticality criticality;
}
