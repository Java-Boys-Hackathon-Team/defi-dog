package ru.javaboys.defidog.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.Comment;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@JmixEntity
@Table(name = "PROTOCOL", indexes = {
        @Index(name = "IDX_PROTOCOL_DEPENDENCY_GRAPH", columnList = "DEPENDENCY_GRAPH_ID"),
        @Index(name = "IDX_PROTOCOL_SOURCES", columnList = "SOURCES_ID")
})
@Entity
@Getter
@Setter
public class DeFiProtocol {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "CMC_ID")
    private Integer cmcId;

    @Column(name = "LOGO_IMAGE")
    private byte[] logoImage;

    @NotBlank
    @InstanceName
    @Comment("Название DeFi-протокола")
    @Column(name = "NAME")
    private String name;

    @Comment("Описание назначения и функций протокола")
    @Column(name = "DESCRIPTION")
    private String description;

    @Comment("Ссылка на официальный сайт")
    @Column(name = "OFFICIAL_SITE")
    private String officialSite;

    @JoinColumn(name = "DEPENDENCY_GRAPH_ID")
    @OneToOne(fetch = FetchType.LAZY)
    private ContractDependenciesGraph dependencyGraph;

    @Comment("Контракты, входящие в протокол")
    @OneToMany(mappedBy = "deFiProtocol")
    private List<SmartContract> contracts;

    @JoinColumn(name = "SOURCES_ID")
    @OneToOne(fetch = FetchType.LAZY)
    private SourceCode sources;

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

    public byte[] getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(byte[] logoImage) {
        this.logoImage = logoImage;
    }

    public Integer getCmcId() {
        return cmcId;
    }

    public void setCmcId(Integer cmcId) {
        this.cmcId = cmcId;
    }

    public SourceCode getSources() {
        return sources;
    }

    public void setSources(SourceCode sources) {
        this.sources = sources;
    }

}
