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
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.OffsetDateTime;
import java.util.UUID;

@JmixEntity
@Table(name = "AUDIT_REPORT", indexes = {
        @Index(name = "IDX_AUDIT_REPORT_SMART_CONTRACT", columnList = "SMART_CONTRACT_ID")
})
@Entity
@Getter
@Setter
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

    @Comment("Краткая характеристика отчета аудита")
    @Column(name = "DESCRIPTION")
    private String description;

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

    @JoinColumn(name = "SMART_CONTRACT_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private SmartContract smartContract;

}
