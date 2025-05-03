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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@JmixEntity
@Table(name = "PROTOCOL", indexes = {
        @Index(name = "IDX_PROTOCOL_DEPENDENCY_GRAPH", columnList = "DEPENDENCY_GRAPH_ID")
})
@Entity
public class DeFiProtocol {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @NotBlank
    @InstanceName
    @Comment("Название DeFi-протокола")
    @Column(name = "NAME")
    private String name;

    @NotBlank
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

    public ContractDependenciesGraph getDependencyGraph() {
        return dependencyGraph;
    }

    public void setDependencyGraph(ContractDependenciesGraph dependencyGraph) {
        this.dependencyGraph = dependencyGraph;
    }

    public List<SmartContract> getContracts() {
        return contracts;
    }

    public void setContracts(List<SmartContract> contracts) {
        this.contracts = contracts;
    }

    public String getOfficialSite() {
        return officialSite;
    }

    public void setOfficialSite(String officialSite) {
        this.officialSite = officialSite;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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