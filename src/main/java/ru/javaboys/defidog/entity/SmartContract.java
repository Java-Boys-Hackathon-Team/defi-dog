package ru.javaboys.defidog.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDeleteInverse;
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
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.OffsetDateTime;
import java.util.UUID;

@JmixEntity
@Table(name = "SMART_CONTRACT", indexes = {
        @Index(name = "IDX_SMART_CONTRACT_DE_FI_PROTOCOL", columnList = "DE_FI_PROTOCOL_ID"),
        @Index(name = "IDX_SMART_CONTRACT_SOURCES", columnList = "SOURCES_ID"),
        @Index(name = "IDX_SMART_CONTRACT", columnList = "ADDRESS"),
        @Index(name = "IDX_SMART_CONTRACT_CRYPTOCURRENCY", columnList = "CRYPTOCURRENCY_ID")
})
@Entity
public class SmartContract {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @NotBlank
    @Comment("Человеко-понятное имя контракта")
    @InstanceName
    @Column(name = "NAME")
    private String name;

    @NotBlank
    @Comment("Адрес смарт-контракта в блокчейне")
    @Column(name = "ADDRESS")
    private String address;

    @Comment("Название сети")
    @Column(name = "NETWORK")
    private String network;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "DE_FI_PROTOCOL_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private DeFiProtocol deFiProtocol;

    @JoinColumn(name = "SOURCES_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private SourceCode sources;

    @Comment("Ссылка на страницу контракта на Etherscan")
    @Column(name = "EXTERNAL_LINK")
    private String externalLink;

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

    @JoinColumn(name = "CRYPTOCURRENCY_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Cryptocurrency cryptocurrency;

    public Cryptocurrency getCryptocurrency() {
        return cryptocurrency;
    }

    public void setCryptocurrency(Cryptocurrency cryptocurrency) {
        this.cryptocurrency = cryptocurrency;
    }

    public SourceCode getSources() {
        return sources;
    }

    public void setSources(SourceCode sources) {
        this.sources = sources;
    }

    public DeFiProtocol getDeFiProtocol() {
        return deFiProtocol;
    }

    public void setDeFiProtocol(DeFiProtocol deFiProtocol) {
        this.deFiProtocol = deFiProtocol;
    }

    public String getExternalLink() {
        return externalLink;
    }

    public void setExternalLink(String externalLink) {
        this.externalLink = externalLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BlockchainNetwork getNetwork() {
        return network == null ? null : BlockchainNetwork.fromId(network);
    }

    public void setNetwork(BlockchainNetwork network) {
        this.network = network == null ? null : network.getId();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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