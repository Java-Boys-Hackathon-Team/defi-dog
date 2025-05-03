package ru.javaboys.defidog.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.Comment;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.OffsetDateTime;
import java.util.UUID;

@JmixEntity
@Table(name = "SCAN_TOOL", indexes = {
        @Index(name = "IDX_SCAN_TOOL_SOURCE_CODE", columnList = "SOURCE_CODE_ID"),
        @Index(name = "IDX_SCAN_TOOL_SOURCE_CODE_SECURITY_SCAN_JOB", columnList = "SOURCE_CODE_SECURITY_SCAN_JOB_ID")
})
@Entity
public class ScanTool {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @NotBlank
    @InstanceName
    @Comment("Название инструмента сканирования исходников")
    @Column(name = "NAME")
    private String name;

    @NotBlank
    @Comment("Имя docker-образа для запуска контейнера")
    @Column(name = "DOCKER_IMAGE")
    private String dockerImage;

    @Comment("Параметры передаваемы при запуске контейнера")
    @Column(name = "CONTAINER_CMD_PARAMS")
    private String containerCmdParams;

    @NotNull
    @Comment("Доступен ли данные инструмент сканирования для запуска")
    @Column(name = "ACTIVE")
    private Boolean active;

    @JoinColumn(name = "SOURCE_CODE_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private SourceCode sourceCode;

    @JoinColumn(name = "SOURCE_CODE_SECURITY_SCAN_JOB_ID")
    @OneToOne(fetch = FetchType.LAZY)
    private SourceCodeSecurityScanJob sourceCodeSecurityScanJob;

    @CreatedBy
    @Column(name = "CREATED_BY")
    private String createdBy;

    @CreatedDate
    @Column(name = "CREATED_DATE")
    private OffsetDateTime createdDate;

    @LastModifiedBy
    @Column(name = "LAST_MODIFIED_BY")
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_DATE")
    private OffsetDateTime lastModifiedDate;

    public SourceCodeSecurityScanJob getSourceCodeSecurityScanJob() {
        return sourceCodeSecurityScanJob;
    }

    public void setSourceCodeSecurityScanJob(SourceCodeSecurityScanJob sourceCodeSecurityScanJob) {
        this.sourceCodeSecurityScanJob = sourceCodeSecurityScanJob;
    }

    public SourceCode getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(SourceCode sourceCode) {
        this.sourceCode = sourceCode;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getContainerCmdParams() {
        return containerCmdParams;
    }

    public void setContainerCmdParams(String containerCmdParams) {
        this.containerCmdParams = containerCmdParams;
    }

    public String getDockerImage() {
        return dockerImage;
    }

    public void setDockerImage(String dockerImage) {
        this.dockerImage = dockerImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OffsetDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(OffsetDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(OffsetDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}