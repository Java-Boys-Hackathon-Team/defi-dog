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

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@JmixEntity
@Table(name = "SMART_CONTRACT", indexes = {
        @Index(name = "IDX_SMART_CONTRACT_DE_FI_PROTOCOL", columnList = "DE_FI_PROTOCOL_ID"),
        @Index(name = "IDX_SMART_CONTRACT_SOURCES", columnList = "SOURCES_ID"),
        @Index(name = "IDX_SMART_CONTRACT", columnList = "ADDRESS")
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

    @Comment("Рыночной цена токена")
    @Column(name = "PRICE", precision = 19, scale = 10)
    private BigDecimal price;

    @Comment("Рыночная капитализация, общая стоимость всех токенов в обращении")
    @Column(name = "MARKET_CAP", precision = 24, scale = 2)
    private BigDecimal marketCap;

    @Comment("Объём торгов за 24 часа")
    @Column(name = "VOLUME", precision = 24, scale = 2)
    private BigDecimal volume;

    @Comment("Циркулирующее предложение, количество токенов, находящихся в обращении на рынке")
    @Column(name = "CIRCULATING_SUPPLY", precision = 30, scale = 10)
    private BigDecimal circulatingSupply;

    @NotBlank
    @Comment("Адрес смарт-контракта в блокчейне")
    @Column(name = "ADDRESS")
    private String address;

    @Comment("Тикер")
    @Column(name = "SYMBOL")
    private String symbol;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "DE_FI_PROTOCOL_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private DeFiProtocol deFiProtocol;

    @Comment("Название сети")
    @Column(name = "NETWORK")
    private String network;

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

    public BigDecimal getCirculatingSupply() {
        return circulatingSupply;
    }

    public void setCirculatingSupply(BigDecimal circulatingSupply) {
        this.circulatingSupply = circulatingSupply;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(BigDecimal marketCap) {
        this.marketCap = marketCap;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
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