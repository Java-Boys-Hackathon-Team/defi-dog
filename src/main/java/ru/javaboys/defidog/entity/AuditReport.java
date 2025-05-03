package ru.javaboys.defidog.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.Comment;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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
@Table(name = "AUDIT_REPORT")
@Entity
public class AuditReport {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @NotBlank
    @InstanceName
    @Comment("Итоговое человеко-понятное описание результатов анализа изменений смарт-контракта и/или ABI")
    @Column(name = "SUMMARY")
    @Lob
    private String summary;

    @NotNull
    @Comment("Уровень критичности")
    @Column(name = "CRITICALITY")
    private String criticality;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "auditReport")
    private SourceCodeChangeSet sourceCodeChangeSet;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "auditReport")
    private AbiChangeSet abiChangeSet;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "auditReport")
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

    public AbiChangeSet getAbiChangeSet() {
        return abiChangeSet;
    }

    public void setAbiChangeSet(AbiChangeSet abiChangeSet) {
        this.abiChangeSet = abiChangeSet;
    }

    public SourceCodeChangeSet getSourceCodeChangeSet() {
        return sourceCodeChangeSet;
    }

    public void setSourceCodeChangeSet(SourceCodeChangeSet sourceCodeChangeSet) {
        this.sourceCodeChangeSet = sourceCodeChangeSet;
    }

    public AuditScanResutlCriticality getCriticality() {
        return criticality == null ? null : AuditScanResutlCriticality.fromId(criticality);
    }

    public void setCriticality(AuditScanResutlCriticality criticality) {
        this.criticality = criticality == null ? null : criticality.getId();
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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