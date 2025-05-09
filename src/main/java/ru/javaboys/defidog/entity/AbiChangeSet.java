package ru.javaboys.defidog.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.Comment;
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
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.OffsetDateTime;
import java.util.UUID;

@JmixEntity
@Table(name = "ABI_CHANGE_SET", indexes = {
        @Index(name = "IDX_ABI_CHANGE_SET_SOURCE_CODE", columnList = "SOURCE_CODE_ID"),
        @Index(name = "IDX_ABI_CHANGE_SET_AUDIT_REPORT", columnList = "AUDIT_REPORT_ID")
})
@Entity
@Getter
@Setter
public class AbiChangeSet {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @NotBlank
    @Comment("Полный ABI в виде JSON")
    @Column(name = "ABI_JSON")
    @Lob
    private String abiJson;

    @NotBlank
    @Comment("Человеко-понятное описание изменений ABI смарт-контракта")
    @Column(name = "CHANGE_SUMMARY")
    @Lob
    private String changeSummary;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "SOURCE_CODE_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private SourceCode sourceCode;

    @NotBlank
    @Comment("Хеш коммита")
    @Column(name = "COMMIT_HASH")
    private String commitHash;

    @Comment("Git diff изменения")
    @Column(name = "GIT_DIFF")
    @Lob
    private String gitDiff;

    @JoinColumn(name = "AUDIT_REPORT_ID")
    @OneToOne(fetch = FetchType.LAZY)
    private AuditReport auditReport;

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
}
