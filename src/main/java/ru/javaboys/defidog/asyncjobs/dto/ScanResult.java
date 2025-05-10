package ru.javaboys.defidog.asyncjobs.dto;


import lombok.Builder;
import lombok.Data;
import ru.javaboys.defidog.entity.SecurityScanJobStatus;

@Data
@Builder
public class ScanResult {
    private SecurityScanJobStatus status;
    private String rawOutput;
    private String errorMessage;
}
